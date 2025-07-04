<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>퍼스널 컬러 AI 추천 시스템</title>

    <!-- CSS 파일 링크 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">

    <!-- 웹 폰트 -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
<div class="container">
    <!-- 헤더 -->
    <div class="header">
        <h1>🎨 퍼스널 컬러 AI 추천</h1>
        <p>당신만의 완벽한 컬러를 찾아보세요</p>
    </div>

    <!-- 자가진단 섹션 -->
    <div class="self-diagnosis">
        <h3>🔍 퍼스널 컬러를 모르겠다면?</h3>
        <p>전문적인 자가진단으로 당신의 퍼스널 컬러를 정확히 찾아보세요!</p>
        <a href="https://mycolor.kr/" target="_blank" class="btn-diagnosis">
            📊 무료 자가진단 받기
        </a>
        <div class="info-text">
            <small>* 새 창에서 진단 후 결과를 확인하고 돌아와 주세요</small>
        </div>
    </div>

    <!-- 구분선 -->
    <div class="divider">
        <span>또는</span>
    </div>

    <!-- 직접 선택 섹션 -->
    <div class="form-section">
        <h3>💡 이미 퍼스널 컬러를 아신다면</h3>

        <form action="${pageContext.request.contextPath}/recommend" method="post" id="colorForm">
            <div class="form-group">
                <label for="personal">퍼스널 컬러 선택:</label>
                <select name="personal" id="personal" required>
                    <option value="">-- 퍼스널 컬러를 선택해주세요 --</option>
                    <optgroup label="🌸 봄 (Spring)">
                        <option value="1">봄 라이트 (Spring Light)</option>
                        <option value="2">봄 브라이트 (Spring Bright)</option>
                    </optgroup>
                    <optgroup label="🌊 여름 (Summer)">
                        <option value="3">여름 라이트 (Summer Light)</option>
                        <option value="4">여름 브라이트 (Summer Bright)</option>
                        <option value="5">여름 뮤트 (Summer Mute)</option>
                    </optgroup>
                    <optgroup label="🍂 가을 (Autumn)">
                        <option value="6">가을 뮤트 (Autumn Mute)</option>
                        <option value="7">가을 스트롱 (Autumn Strong)</option>
                        <option value="8">가을 딥 (Autumn Deep)</option>
                    </optgroup>
                    <optgroup label="❄️ 겨울 (Winter)">
                        <option value="9">겨울 브라이트 (Winter Bright)</option>
                        <option value="10">겨울 딥 (Winter Deep)</option>
                    </optgroup>
                </select>
            </div>

            <button type="submit" class="btn-submit" id="submitBtn" disabled>
                🎯 AI 맞춤 상품 추천받기
            </button>
        </form>
    </div>

    <!-- 안내 메시지 -->
    <div class="info-text">
        <p>🤖 AI가 당신의 퍼스널 컬러에 완벽하게 어울리는 패션 아이템을 찾아드립니다</p>
    </div>
</div>

<!-- JavaScript 파일 링크 -->
<script src="${pageContext.request.contextPath}/js/index.js"></script>
</body>
</html>