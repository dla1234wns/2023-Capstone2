from fastapi import APIRouter, FastAPI, HTTPException, status
from requests import Session
from fastapi.params import Depends
from sqlalchemy import desc
from db import get_db
from pydantic import BaseModel

import models, schemas, datetime, statusCode, random
import json

router = APIRouter(prefix="/device",tags=["device"])

@router.post("/device_Add/{user_id}/{device_no}/{device_name}/{device_sn}", status_code = 200)
async def device_Add(user_id: str, device_no: int, device_name: str, device_sn:str, db: Session = Depends(get_db)):
    """
    디바이스 등록 API
    """
    user_exist = db.query(models.User).filter_by(user_id=user_id).first()
    device_exist = db.query(models.Device).filter_by(device_sn = device_sn).first()

    if device_exist == None:
        return statusCode.not_sn
    elif device_exist.user_id is not None:
        return statusCode.id_duplication_error
    elif device_exist is not None and device_exist.user_id == None and device_exist.device_no == None:
        user_exist.HDCount += 1
        db.commit()
        db.query(models.Device).filter(models.Device.device_sn == device_sn).update({
            models.Device.device_no: device_no,
            models.Device.user_id: user_exist.id,
            models.Device.device_name: device_name,
            models.Device.device_value: None
        })
        db.commit()
        return statusCode.success
    elif device_exist is not None and device_exist.user_id == None and device_exist.device_no is not None:
        user_exist.HDCount += 1
        db.commit()
        db.query(models.Device).filter(models.Device.device_sn == device_sn).update({
            models.Device.user_id: user_exist.id,
            models.Device.device_name: device_name,
            models.Device.device_value: None
        })
        db.commit()
        return statusCode.success
    else:
        return statusCode.unexpected_error
    
# 하드웨어 정보 받아오기
@router.get("/device_Info/{user_id}", status_code = 200)
async def device_Info(user_id: str, db: Session = Depends(get_db)):
    """
    디바이스 정보 API
    """
    user = db.query(models.User).filter_by(user_id=user_id).first()

    if not user:
        return statusCode.not_id

    device_info_exist = db.query(models.Device).filter_by(user_id=user.id).all()

    if not device_info_exist:
        return statusCode.not_id
    result_device_info = {
        "device_info_exist": str(len(device_info_exist)),
    }
    for i, device in enumerate(device_info_exist):
        result_device_info[f"device_no{i}"] = device.device_no
        result_device_info[f"device_name{i}"] = device.device_name
        result_device_info[f"device_sn{i}"] = device.device_sn
        result_device_info[f"device_value{i}"] = device.device_value

    result_device_info["SUCCESS"] = str(200)

    return result_device_info

@router.put("/switch_change/{user_id}/{device_sn}/{device_value}", status_code = 200)
async def switch_change(user_id: str, device_sn: str, device_value: int, db:Session = Depends(get_db)):
    """
    릴레이, 리드 스위치 변경 API
    """
    user_id = db.query(models.User.id).filter_by(user_id=user_id).first()
    device_id = db.query(models.Device).filter_by(user_id=user_id.id).first()

    if device_id is not None:
        db.query(models.Device).filter(models.Device.device_sn == device_sn).update({"device_value": device_value})
        db.commit()
        return statusCode.success
    elif device_id == None:
        return statusCode.not_matching
    else:
        return statusCode.unexpected_error
    
@router.delete("/device_delete/{user_id}/{device_sn}", status_code = 200)
async def device_delete(user_id: str, device_sn:str, db: Session = Depends(get_db)):
    """
    디바이스 제거 API
    """
    user_exist = db.query(models.User).filter_by(user_id=user_id).first()
    device_sn_exist = db.query(models.Device).filter_by(user_id=user_exist.id).first()
    
    if device_sn_exist.id:
        user_exist.HDCount -= 1
        db.commit()
        db.query(models.Device).filter(models.Device.device_sn == device_sn).update({
            models.Device.device_no: None,
            models.Device.user_id: None,
            models.Device.device_name: None,
            models.Device.device_value: None
        })
        db.commit()
        return statusCode.success
    else:
        return statusCode.unexpected_error