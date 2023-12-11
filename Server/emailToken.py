import smtplib, secrets
from jose import JWTError, jwt
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

# 고정된 시크릿 키 생성
SECRET_KEY = secrets.token_urlsafe(32)

def create_email_verification_token(user_email):
    # 랜덤한 32자 길이의 토큰 생성
    verification_token = jwt.encode({'user_email': user_email}, SECRET_KEY, algorithm='HS256')
    return verification_token

def send_email_verification_email(user_email, create_email_verification_token):
    # Gmail 설정
    gmail_user = "1961049@pcu.ac.kr"
    gmail_password = "dlawnsgud1@"

    # 이메일 구성
    subject = "이메일 주소 확인을 위한 링크"
    body = f"이메일 주소를 확인하려면 다음 링크를 클릭하세요: http://203.250.133.141:8080/user/verify_email/{create_email_verification_token}"

    msg = MIMEMultipart()
    msg['From'] = gmail_user
    msg['To'] = user_email
    msg['Subject'] = subject

    # 본문 추가
    msg.attach(MIMEText(body, 'plain'))

    # SMTP 서버 설정 및 연결
    with smtplib.SMTP_SSL('smtp.gmail.com', 465) as server:
        server.login(gmail_user, gmail_password)
        server.sendmail(gmail_user, user_email, msg.as_string())

def verify_email_verification_token(token):
    try:
        secret_key = SECRET_KEY
        payload = jwt.decode(token, secret_key, algorithms=['HS256'])
        return payload.get('user_email')
    except jwt.ExpiredSignatureError:
        # 토큰 만료
        return None
    except jwt.InvalidTokenError:
        # 잘못된 토큰
        return None