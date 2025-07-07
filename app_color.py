# 퍼스널 컬러 분석 AI서버

from flask import Flask, request, jsonify
from flask_cors import CORS
import os
import requests
from dotenv import load_dotenv
import json

load_dotenv()

app = Flask(__name__)
CORS(app)

# OpenAI API 키 설정
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")


def analyze_fashion_color(image_url):
    """OpenAI Vision API로 패션 아이템 색상 분석"""

    if not OPENAI_API_KEY:
        return {"error": "OpenAI API 키가 설정되지 않았습니다"}

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {OPENAI_API_KEY}",
    }

    # 퍼스널 컬러 분석 프롬프트 (PCCS 색채계 기반)
    prompt = """
    이 패션 상품 이미지를 분석하여 퍼스널 컬러 분류를 해주세요.

    **PCCS 색채계 기반 퍼스널 컬러 분류:**
    
    🌸 **봄 계열 (따뜻하고 밝은 색상)**
    - 봄 라이트: 연핑크, 피치, 연노랑, 연초록 등 밝고 따뜻한 파스텔
    - 봄 브라이트: 코랄, 오렌지, 밝은 초록, 터콰이즈 등 선명하고 따뜻한 색상
    
    🌊 **여름 계열 (차가우면서 부드러운 색상)**
    - 여름 라이트: 라벤더, 연파랑, 민트, 연핑크 등 부드럽고 차가운 파스텔
    - 여름 브라이트: 로얄블루, 퍼플, 매젠타 등 선명하고 차가운 색상
    - 여름 뮤트: 그레이, 네이비, 스모키 블루 등 차분하고 차가운 중성색
    
    🍂 **가을 계열 (따뜻하고 깊은 색상)**
    - 가을 뮤트: 베이지, 카키, 브라운 등 차분하고 따뜻한 중성색
    - 가을 스트롱: 머스타드, 올리브, 버건디 등 진하고 따뜻한 색상
    - 가을 딥: 다크브라운, 와인, 딥그린 등 깊고 따뜻한 색상
    
    ❄️ **겨울 계열 (차가우면서 강렬한 색상)**
    - 겨울 브라이트: 순백, 빨강, 파랑 등 선명하고 차가운 색상
    - 겨울 딥: 블랙, 네이비, 다크퍼플 등 깊고 차가운 색상

    **JSON 형식으로 응답해주세요:**
    {
        "dominant_colors": ["#색상1", "#색상2", "#색상3"],
        "personal_color": "분류명",
        "confidence": 85,
        "reason": "색상 분석 근거"
    }
    """

    payload = {
        "model": "gpt-4o",
        "messages": [
            {
                "role": "user",
                "content": [
                    {"type": "text", "text": prompt},
                    {"type": "image_url", "image_url": {"url": image_url}},
                ],
            }
        ],
        "max_tokens": 500,
    }

    try:
        response = requests.post(
            "https://api.openai.com/v1/chat/completions",
            headers=headers,
            json=payload,
            timeout=30,
        )

        if response.status_code == 200:
            result = response.json()
            content = result["choices"][0]["message"]["content"]

            # JSON 파싱 시도
            try:
                json_result = json.loads(content)
                return json_result
            except json.JSONDecodeError:
                # JSON 파싱 실패시 텍스트에서 정보 추출
                return parse_text_response(content)
        else:
            return {
                "error": f"API 호출 실패: {response.status_code}",
                "details": response.text,
            }

    except Exception as e:
        return {"error": f"분석 중 오류: {str(e)}"}


def parse_text_response(text):
    """JSON 파싱 실패시 텍스트에서 정보 추출"""
    personal_colors = [
        "봄 라이트",
        "봄 브라이트",
        "여름 라이트",
        "여름 브라이트",
        "여름 뮤트",
        "가을 뮤트",
        "가을 스트롱",
        "가을 딥",
        "겨울 브라이트",
        "겨울 딥",
    ]

    detected_color = "알 수 없음"
    for color in personal_colors:
        if color in text:
            detected_color = color
            break

    return {
        "dominant_colors": ["#000000", "#FFFFFF", "#808080"],
        "personal_color": detected_color,
        "confidence": 50,
        "reason": "텍스트에서 추출된 분석 결과",
    }


@app.route("/analyze-color", methods=["POST"])
def analyze_color():
    """색상 분석 API 엔드포인트"""
    try:
        data = request.json
        image_url = data.get("image_url")

        if not image_url:
            return jsonify({"error": "이미지 URL이 필요합니다"}), 400

        print(f"🔍 분석 시작: {image_url}")

        # OpenAI Vision API 호출
        result = analyze_fashion_color(image_url)

        print(f"✅ 분석 완료: {result}")

        return jsonify(result)

    except Exception as e:
        print(f"❌ 오류 발생: {str(e)}")
        return jsonify({"error": str(e)}), 500


@app.route("/health", methods=["GET"])
def health_check():
    """서버 상태 확인"""
    return jsonify(
        {
            "status": "healthy",
            "message": "AI 색상 분석 서버가 정상 작동 중입니다",
            "api_key_configured": bool(OPENAI_API_KEY),
        }
    )


if __name__ == "__main__":
    print("🎨 AI 색상 분석 서버 시작...")
    print("💡 OpenAI API 키 확인:", "✅ 설정됨" if OPENAI_API_KEY else "❌ 없음")

    if not OPENAI_API_KEY:
        print("⚠️  .env 파일에 OPENAI_API_KEY를 설정해주세요!")

    app.run(debug=True, host="0.0.0.0", port=8000)
