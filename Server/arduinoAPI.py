from fastapi import APIRouter, FastAPI, HTTPException, status
from requests import Session
from fastapi.params import Depends
from sqlalchemy import desc
from db import get_db
from pydantic import BaseModel

import models, schemas, datetime, statusCode

router = APIRouter(prefix="/arduino",tags=["arduino"])

#온습도, 릴레이, 리드 값 서버에 전송
@router.put("/sensor_upload/{device_no}/{device_sn}/{device_value}/", status_code=200)
async def sensor_upload(device_sn:str, device_no:int, device_value:str, db:Session = Depends(get_db)):
    """
    온습도, 릴레이, 리드 값 DB 저장 API
    """
    device_exist = db.query(models.Device).filter_by(device_sn = device_sn).first()

    if device_exist.device_sn is not None:
        db.query(models.Device).filter(models.Device.device_sn == device_sn).update({
            models.Device.device_no: device_no,
            models.Device.device_value: device_value
        })
        db.commit()
        return statusCode.success
    else:
        return statusCode.unexpected_error

# 센서값 넘겨주기
@router.get("/sensor_get/{device_sn}/", status_code=200)
async def sensor_get(device_sn:str, db:Session = Depends(get_db)):
    """
    센서 값 넘겨주기 API
    """

    device_exist = db.query(models.Device).filter_by(device_sn = device_sn).first()

    if device_exist == None:
        return statusCode.not_sn
    elif device_exist != None:
        result = {"device_sn" : device_exist.device_sn, "device_value" : device_exist.device_value , "SUCCESS" : 200}
        return result
    else:
        return statusCode.unexpected_error