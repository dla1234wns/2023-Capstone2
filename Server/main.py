import uvicorn
from typing import Union
from fastapi import FastAPI
from pydantic import BaseModel
from starlette.responses import RedirectResponse

from requests import Session
from fastapi.params import Depends

import models, schemas, userAPI, deviceAPI, arduinoAPI

from db import enigne, get_db
models.Base.metadata.create_all(bind=enigne)

app = FastAPI()

@app.get("/")
def main():
    return RedirectResponse(url="/docs")

app.include_router(userAPI.router)
app.include_router(deviceAPI.router)
app.include_router(arduinoAPI.router)

if __name__  == '__main__':
    uvicorn.run(app="main:app", 
                host="203.250.133.141",
                port=8080,
                reload=True)
    
#uvicorn main:app --host 203.250.133.141 --port 8080 --reload
#.\venv\Scripts\activate
# FLUSH PRIVILEGES;