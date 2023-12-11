from enum import unique
from operator import index
from pymysql import Timestamp
from sqlalchemy import Boolean, Column, ForeignKey, Integer, String, func, DateTime, Enum
from sqlalchemy.orm import relationship
from db import Base
from sqlalchemy.orm import Session

class BaseMixin:
    id = Column(Integer, primary_key=True, index=True, unique=True)
    created_at = Column(DateTime, nullable=False, default=func.utc_timestamp())
    updated_at = Column(DateTime, nullable=False, default=func.utc_timestamp(), onupdate=func.utc_timestamp())

    def __init__(self):
        self._q = None
        self._session = None
        self.served = None

    def all_columns(self):
        return [c for c in self.__table__.columns if c.primary_key is False and c.name != "created_at"]

    def __hash__(self):
        return hash(self.id)

    @classmethod
    def create(cls, db: Session, auto_commit=False, **kwargs):
        """
        테이블 데이터 적재 전용 함수
        :param session:
        :param auto_commit: 자동 커밋 여부
        :param kwargs: 적재 할 데이터
        :return:
        """
        obj = cls()
        for col in obj.all_columns():
            col_name = col.name
            if col_name in kwargs:
                setattr(obj, col_name, kwargs.get(col_name))
        db.add(obj)
        db.flush()
        if auto_commit:
            db.commit()
        return obj

"""
기기 넘버 하나당 하나의 디바이스가 나와야함
디바이스 넘버로 참조해서 그 디바이스 넘버가 가지고있는 센서값을 뽑아야함
"""

class User(Base, BaseMixin):
    __tablename__ = 'user'

    user_id = Column(String(length=255), unique = True)
    user_pw = Column(String(length=255))
    user_name = Column(String(length=255))
    user_email = Column(String(length=255), unique = True)
    emailCheck = Column(Integer)
    HDCount = Column(Integer) # 기기 갯수 디폴트를 0, 기기를 등록할때마다 +1

    device  = relationship(  #연결하고자 하는 테이블명
        "Device",   #클래스명
        back_populates="user",  #테이블명
        cascade="all, delete",
        passive_deletes=True,
    )

class Device(Base, BaseMixin):
    __tablename__ = 'device'
    user_id = Column(Integer, ForeignKey('user.id',ondelete="CASCADE"))
    device_no = Column(Integer)
    device_name = Column(String(length=255))
    device_sn = Column(String(length=255), unique = True)
    device_value = Column(Integer)

    user = relationship("User",back_populates="device") #연결 받고자 하는것