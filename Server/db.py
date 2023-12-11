from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

URL = 'mysql+pymysql://root:0000@localhost:3306/dream'
enigne = create_engine(URL,pool_pre_ping=True)

SessionLocal = sessionmaker(autocommit=False,autoflush=False, bind=enigne)

def get_db():
    try:
        db = SessionLocal()
        yield db
    finally:
        db.close()

Base = declarative_base()