// index.js

// 페이지 로딩 시 실행
document.addEventListener('DOMContentLoaded', function() {
    // 폼 요소들
    const personalSelect = document.getElementById('personal');
    const submitBtn = document.getElementById('submitBtn');
    const colorForm = document.getElementById('colorForm');

    // 자가진단 버튼
    const diagnosisBtn = document.querySelector('.btn-diagnosis');

    // 선택 변경 이벤트
    personalSelect.addEventListener('change', function() {
        const selectedValue = this.value;

        if (selectedValue) {
            // 선택되면 버튼 활성화
            submitBtn.disabled = false;
            submitBtn.textContent = '🎯 AI 맞춤 상품 추천받기';

            // 선택된 옵션에 따라 색상 표시
            showSelectedColorInfo(selectedValue);
        } else {
            // 선택 해제되면 버튼 비활성화
            submitBtn.disabled = true;
            submitBtn.textContent = '🎯 AI 맞춤 상품 추천받기';

            // 색상 정보 숨기기
            hideColorInfo();
        }
    });

    // 폼 제출 시 로딩 표시
    colorForm.addEventListener('submit', function(e) {
        const selectedValue = personalSelect.value;

        if (!selectedValue) {
            e.preventDefault();
            alert('퍼스널 컬러를 선택해주세요!');
            return;
        }

        // 로딩 상태 표시
        showLoadingState();
    });

    // 자가진단 버튼 클릭 추적
    diagnosisBtn.addEventListener('click', function() {
        // 구글 애널리틱스나 다른 분석 도구를 위한 이벤트 추적
        console.log('자가진단 페이지로 이동');

        // 로컬 스토리지에 방문 기록 저장 (선택사항)
        localStorage.setItem('diagnosisClicked', new Date().toISOString());
    });

    // 페이지 로딩 애니메이션
    animatePageLoad();
});

/**
 * 선택된 색상 정보 표시
 */
function showSelectedColorInfo(selectedValue) {
    const colorInfo = getColorInfo(selectedValue);

    // 기존 색상 정보 제거
    hideColorInfo();

    // 새로운 색상 정보 생성
    const infoDiv = document.createElement('div');
    infoDiv.className = 'selected-color-info';
    infoDiv.innerHTML = `
        <div style="background: ${colorInfo.gradient}; padding: 15px; border-radius: 10px; margin: 15px 0; text-align: center;">
            <h4 style="color: white; margin: 0; text-shadow: 1px 1px 2px rgba(0,0,0,0.3);">
                ${colorInfo.name}
            </h4>
            <p style="color: white; margin: 5px 0 0 0; font-size: 0.9em; opacity: 0.9;">
                ${colorInfo.description}
            </p>
        </div>
    `;

    // 폼 그룹 뒤에 추가
    const formGroup = document.querySelector('.form-group');
    formGroup.appendChild(infoDiv);
}

/**
 * 색상 정보 숨기기
 */
function hideColorInfo() {
    const existingInfo = document.querySelector('.selected-color-info');
    if (existingInfo) {
        existingInfo.remove();
    }
}

/**
 * 색상 정보 가져오기
 */
function getColorInfo(value) {
    const colorMap = {
        '1': {
            name: '봄 라이트',
            gradient: 'linear-gradient(135deg, #FFB6C1, #FFE4E1)',
            description: '밝고 따뜻한 파스텔 톤'
        },
        '2': {
            name: '봄 브라이트',
            gradient: 'linear-gradient(135deg, #FF6B6B, #FFA500)',
            description: '선명하고 활기찬 봄 색상'
        },
        '3': {
            name: '여름 라이트',
            gradient: 'linear-gradient(135deg, #E6E6FA, #B0E0E6)',
            description: '부드럽고 차가운 파스텔'
        },
        '4': {
            name: '여름 브라이트',
            gradient: 'linear-gradient(135deg, #4169E1, #FF1493)',
            description: '선명하고 차가운 색상'
        },
        '5': {
            name: '여름 뮤트',
            gradient: 'linear-gradient(135deg, #708090, #B0C4DE)',
            description: '차분하고 부드러운 색상'
        },
        '6': {
            name: '가을 뮤트',
            gradient: 'linear-gradient(135deg, #D2691E, #CD853F)',
            description: '따뜻하고 차분한 색상'
        },
        '7': {
            name: '가을 스트롱',
            gradient: 'linear-gradient(135deg, #B22222, #FF8C00)',
            description: '깊고 강렬한 가을 색상'
        },
        '8': {
            name: '가을 딥',
            gradient: 'linear-gradient(135deg, #8B4513, #A0522D)',
            description: '깊고 풍부한 색상'
        },
        '9': {
            name: '겨울 브라이트',
            gradient: 'linear-gradient(135deg, #FF0000, #0000FF)',
            description: '선명하고 강렬한 색상'
        },
        '10': {
            name: '겨울 딥',
            gradient: 'linear-gradient(135deg, #000000, #2F4F4F)',
            description: '깊고 시원한 색상'
        }
    };

    return colorMap[value] || {
        name: '알 수 없음',
        gradient: 'linear-gradient(135deg, #ccc, #999)',
        description: '색상 정보 없음'
    };
}

/**
 * 로딩 상태 표시
 */
function showLoadingState() {
    const submitBtn = document.getElementById('submitBtn');
    const originalText = submitBtn.textContent;

    submitBtn.disabled = true;
    submitBtn.innerHTML = '🔄 AI 분석 중...';

    // 로딩 애니메이션 스타일 추가
    submitBtn.style.background = 'linear-gradient(135deg, #ccc, #999)';

    // 실제 제출은 계속 진행
    setTimeout(() => {
        submitBtn.innerHTML = '✨ 추천 상품 준비 중...';
    }, 1000);
}

/**
 * 페이지 로딩 애니메이션
 */
function animatePageLoad() {
    const container = document.querySelector('.container');

    // 초기 상태 설정
    container.style.opacity = '0';
    container.style.transform = 'translateY(30px)';

    // 애니메이션 실행
    setTimeout(() => {
        container.style.transition = 'opacity 0.8s ease, transform 0.8s ease';
        container.style.opacity = '1';
        container.style.transform = 'translateY(0)';
    }, 100);
}

/**
 * 로컬 스토리지 유틸리티
 */
const Storage = {
    set: function(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
        } catch (e) {
            console.warn('로컬 스토리지 저장 실패:', e);
        }
    },

    get: function(key) {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : null;
        } catch (e) {
            console.warn('로컬 스토리지 읽기 실패:', e);
            return null;
        }
    }
};

// 페이지 언로드 시 정리
window.addEventListener('beforeunload', function() {
    console.log('페이지 종료 중...');
});