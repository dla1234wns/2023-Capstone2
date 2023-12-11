from xmlrpc.client import DateTime
from pydantic import BaseModel, EmailStr

class User(BaseModel):
    user_id: str
    user_pw: str
    user_name: str
    user_email: str
    emailCheck: int
    HDCount: int

    class Config:
        orm_mode = True

class Device(BaseModel):
    user_id: int
    device_no: int
    device_name: str
    device_sn: str
    device_value: int

    class Config:
        orm_mode = True