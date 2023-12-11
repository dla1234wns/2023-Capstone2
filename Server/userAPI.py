from fastapi import APIRouter, FastAPI, HTTPException, status
from requests import Session
from fastapi.params import Depends
from sqlalchemy import desc
from db import get_db
from pydantic import BaseModel
from email_validator import validate_email, EmailNotValidError
from emailToken import create_email_verification_token, send_email_verification_email, verify_email_verification_token

import models, schemas, datetime, statusCode, random

router = APIRouter(prefix="/user",tags=["user"])

async def is_login_id_exist(login_id_str: str,db:Session = Depends(get_db)):
    #같은 id가 있는지 확인하는 함수
    get_login_id = db.query(models.User.user_id).filter_by(user_id=login_id_str).first()
    if get_login_id:
        return True
    else:
        return False
    

@router.post("/get_certification_number/{user_email}")
async def signup(user_email: str):
    """
    인증 이메일 발송 API
    """
    try:
        validate_email(user_email)
    except EmailNotValidError:
        raise HTTPException(status_code=400, detail="Invalid email address")

    token = create_email_verification_token(user_email)
    send_email_verification_email(user_email, token)

    return statusCode.success

@router.get("/verify_email/{token}",status_code=200)
async def verify_email(token: str, db:Session = Depends(get_db)):
    """
    이메일 인증
    """
    mail = verify_email_verification_token(token)
    email_check = db.query(models.User.user_email).filter_by(user_email = mail).first()
    userId_check = db.query(models.User.user_id).filter_by(user_email = mail).first()
    certification_number = random.randint(100000, 999999)

    if mail is None:
        raise HTTPException(status_code=400, detail="Invalid or expired token")
    elif userId_check: # ID가 db에 있을 경우
        db.query(models.User).filter_by(user_email = mail).update({"emailCheck":certification_number})
        db.commit()
        return {"certification_number" : str(certification_number)}
    elif email_check: # 이메일이 db에 있을 경우
        db.query(models.User).filter_by(user_email = mail).update({"emailCheck":certification_number})
        db.commit()
        return {"certification_number" : str(certification_number)}
    else: # 이메일이 db에 없는 경우
        models.User.create(db, auto_commit = True, user_email = mail, emailCheck = certification_number)
        return {"certification_number" : str(certification_number)}
    
@router.get("/check_verification/{email}/{emailCheck}", status_code=200)
async def check_verification(email:str, emailCheck:int, db:Session = Depends(get_db)):
    """
    인증번호 확인 API
    """
    user_exist = db.query(models.User).filter_by(user_email = email).first()

    if user_exist:
        if user_exist.emailCheck == emailCheck:
            return statusCode.success
        else:
            return statusCode.certification_Number
    elif user_exist == None:
        return statusCode.not_email
    else:
        return statusCode.unexpected_error
# 회원가입
@router.post("/regist/{user_id}/{user_pw}/{user_name}/{user_email}/", status_code=200)
async def regist(user_id: str, user_pw:str, user_name:str, user_email:str, db:Session = Depends(get_db)):
    """
    회원가입 API
    """
    id_exist = db.query(models.User.user_id).filter_by(user_id=user_id).first()
    email_exist = db.query(models.User.user_email).filter_by(user_email=user_email).first()
    user = db.query(models.User).filter_by(user_id = user_id, user_pw = user_pw, user_name = user_name, user_email = user_email).first()

    if not user_id:
        return statusCode.signup_error
    elif not user_pw:
        return statusCode.signup_error
    elif email_exist: # 이메일이 존재하면 이미 있는 행 지우고 해당 이메일 행 다시 생성
        db.query(models.User).filter(models.User.user_email == user_email).delete()
        db.commit()
        models.User.create(db, auto_commit = True, user_id = user_id, user_pw = user_pw, user_name = user_name, user_email = user_email, emailCheck = None, HDCount = 0)
        return statusCode.success
    elif id_exist:
        return statusCode.id_duplication_error
    
    if user == None:
        models.User.create(db, auto_commit = True, user_id = user_id, user_pw = user_pw, user_name = user_name, user_email = user_email, emailCheck = None, HDCount = 0)
        return statusCode.success
    else:
        return statusCode.unexpected_error

@router.get("/id_check/{user_id}",status_code=200)
async def id_check(user_id: str,db:Session = Depends(get_db)): 
    """
    아이디 중복 확인 API
    """
    is_exist = db.query(models.User.user_id).filter_by(user_id=user_id).first()
    
    if is_exist:
        return statusCode.id_duplication_error
    elif None == is_exist:
        return statusCode.success
    else:
        return statusCode.unexpected_error

@router.get("/login/{user_id}/{user_pw}", status_code=200)
async def login(user_id: str, user_pw:str, db:Session = Depends(get_db)):
    """
    로그인 API
    """
    id = db.query(models.User.user_id).filter_by(user_id=user_id).first()
    pw = db.query(models.User.user_pw).filter_by(user_pw=user_pw).first()

    if id == None:
        return statusCode.not_id
    elif id != None and pw == None:
        return statusCode.login_pw_error
    elif id != None and pw != None:
        return statusCode.success
    else:
        return statusCode.unexpected_error

# 아이디 찾기
@router.get("/id_find/{user_name}/{user_email}", status_code=200)
async def id_find(user_name: str, user_email:str, db:Session = Depends(get_db)):
    """
    아이디 찾기 API
    """
    name = db.query(models.User.user_name).filter_by(user_name = user_name).first()
    email = db.query(models.User.user_email).filter_by(user_email = user_email).first()

    if name != None and email != None:
        id = db.query(models.User.user_id).filter_by(user_name = user_name, user_email = user_email).first()
        result = {"user_id" : id.user_id, "SUCCESS" : 200}
        return result
    elif name == None:
        return statusCode.not_name
    elif email == None:
        return statusCode.not_email
    else:
        return statusCode.unexpected_error

# 비밀번호 찾기(비밀번호 변경)
@router.put("/pw_update/{user_id}/{new_pw}/{new_pw_check}", status_code=200)
async def pw_update(user_id:str, new_pw:str, new_pw_check:str, db:Session = Depends(get_db)):
    """
    비밀번호 변경 API
    """
    
    id = db.query(models.User.user_id).filter_by(user_id = user_id).first()
    pw = db.query(models.User.user_pw).filter_by(user_id = user_id).first()

    if id != None and pw != None:
        db.query(models.User).filter(models.User.user_id == user_id).update({"user_pw":new_pw})
        db.commit()
        return statusCode.success
    elif id == None:
        return statusCode.not_id
    elif new_pw != new_pw_check:
        return statusCode.not_equal_new_pw
    else:
        return statusCode.unexpected_error

# 유저 정보 받아오기(설정)
@router.get("/user_info/{user_id}", status_code = 200)
async def user_info(user_id: str, db:Session = Depends(get_db)):
    """
    user_info API
    """
    user_info_exist = db.query(models.User).filter_by(user_id = user_id).first()

    if user_info_exist == None:
        return statusCode.not_id
    elif user_info_exist != None:
        result = {"user_id" : user_info_exist.user_id, "user_pw" : user_info_exist.user_pw, "user_name" : user_info_exist.user_name, "user_email" : user_info_exist.user_email, "HDCount" : user_info_exist.HDCount, "SUCCESS" : 200}
        return result
    else:
        return statusCode.unexpected_error

# 게정 삭제
@router.delete("/user_delete/{user_id}/{user_pw}",status_code = 200)
async def user_delete(user_id: str, user_pw:str, db:Session = Depends(get_db)):
    """
    계정 삭제 API
    """
    exist_user_id = db.query(models.User).filter_by(user_id = user_id).first()

    exist_user_pw = db.query(models.User.user_pw).filter_by(user_pw = user_pw).first()

    if exist_user_id and exist_user_pw:
        db.query(models.Device).filter(models.Device.user_id == exist_user_id.id).update({
            models.Device.user_id: None,
            models.Device.device_name: None
        })
        db.commit()
        db.query(models.User).filter(models.User.user_pw == user_pw).delete()
        db.commit()
        return statusCode.success
    elif exist_user_id == None:
        return statusCode.not_id
    elif exist_user_pw == None:
        return statusCode.login_pw_error
    else:
        return statusCode.unexpected_error