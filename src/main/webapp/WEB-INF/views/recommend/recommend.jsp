<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${personalColor} AI 맞춤 추천</title>

    <!-- CSS 파일 링크 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/recommend.css">

    <!-- 웹 폰트 (선택사항) -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
<div class="container">
    <!-- 헤더 섹션 -->
    <div class="header">
        <h1>🎨 ${personalColor} AI 맞춤 추천</h1>
        <p>OpenAI Vision API가 분석한 당신만의 특별한 신상 컬렉션</p>
        <div class="ai-badge">🤖 AI 색상 분석 적용</div>
    </div>

    <!-- 통계 섹션 -->
    <div class="stats">
        <div class="stats-grid">
            <div class="stat-item">
                <div class="stat-number">${totalCount}</div>
                <div class="stat-label">AI 추천 상품</div>
            </div>
            <div class="stat-item">
                <div class="stat-number">${processingTime}s</div>
                <div class="stat-label">분석 시간</div>
            </div>
            <div class="stat-item">
                <div class="stat-number">${personalColor}</div>
                <div class="stat-label">퍼스널 컬러</div>
            </div>
        </div>
    </div>

    <!-- 메시지 섹션 -->
    <div class="messages">
        <c:if test="${not empty successMessage}">
            <div class="message success-message">
                ✅ ${successMessage}
            </div>
        </c:if>
        <c:if test="${not empty infoMessage}">
            <div class="message info-message">
                ℹ️ ${infoMessage}
            </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="message error-message">
                ⚠️ ${errorMessage}
            </div>
        </c:if>
    </div>

    <!-- 상품 섹션 -->
    <div class="products-section">
        <c:choose>
            <c:when test="${not empty items and totalCount > 0}">
                <h2>🛍️ AI가 엄선한 ${personalColor} 맞춤 상품</h2>
                <div class="product-grid">
                    <c:forEach var="item" items="${items}" varStatus="status">
                        <div class="product-card">
                            <img src="${item.imageUrl}"
                                 alt="${item.name}"
                                 class="product-image"
                                 onerror="this.src='https://via.placeholder.com/300x250?text=이미지+없음'">
                            <div class="product-info">
                                <div class="product-name">${item.name}</div>
                                <div class="product-actions">
                                    <a href="${item.productLink}"
                                       target="_blank"
                                       class="btn btn-primary">
                                        상품 보러가기 →
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="no-products">
                    <div class="spinner"></div>
                    <h3>🔍 ${personalColor} 톤 상품 분석 중...</h3>
                    <p>AI가 당신의 퍼스널 컬러에 완벽하게 어울리는 상품들을 찾고 있습니다.</p>
                    <p><small>💡 색상 분석에는 시간이 소요될 수 있습니다. 잠시만 기다려주세요!</small></p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- 뒤로가기 섹션 -->
    <div class="back-section">
        <a href="${pageContext.request.contextPath}/" class="btn btn-back">
            ← 다른 퍼스널 컬러로 다시 추천받기
        </a>
    </div>
</div>

<!-- JavaScript 파일 링크 -->
<script src="${pageContext.request.contextPath}/js/recommend.js"></script>
</body>
</html>