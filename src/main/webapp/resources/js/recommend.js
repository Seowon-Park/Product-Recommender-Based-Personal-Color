// recommend.js

// 페이지 로딩 시 실행
document.addEventListener('DOMContentLoaded', function() {
    // 페이지 로딩 애니메이션
    animateProductCards();

    // 이미지 클릭 이벤트 추가
    setupImageClickEvents();

    // 통계 숫자 애니메이션
    animateStatNumbers();

    // 로딩 상태 확인 및 처리
    handleLoadingState();
});

/**
 * 상품 카드 애니메이션
 */
function animateProductCards() {
    const cards = document.querySelectorAll('.product-card');

    cards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(30px)';

        setTimeout(() => {
            card.style.transition = 'opacity 0.8s ease, transform 0.8s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 150);
    });
}

/**
 * 이미지 클릭 이벤트 설정
 */
function setupImageClickEvents() {
    document.querySelectorAll('.product-image').forEach(img => {
        img.addEventListener('click', function() {
            // 이미지를 새 창에서 열기
            window.open(this.src, '_blank');
        });

        // 이미지 호버 효과
        img.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.05)';
        });

        img.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
        });
    });
}

/**
 * 통계 숫자 카운트 애니메이션
 */
function animateNumber(element, target) {
    let current = 0;
    const increment = target / 50;
    const timer = setInterval(() => {
        current += increment;
        if (current >= target) {
            current = target;
            clearInterval(timer);
        }
        element.textContent = Math.floor(current);
    }, 20);
}

/**
 * 통계 숫자 애니메이션 실행
 */
function animateStatNumbers() {
    document.querySelectorAll('.stat-number').forEach(el => {
        const target = parseInt(el.textContent);
        if (!isNaN(target)) {
            animateNumber(el, target);
        }
    });
}

/**
 * 로딩 상태 처리
 */
function handleLoadingState() {
    const spinner = document.querySelector('.spinner');
    const noProducts = document.querySelector('.no-products');

    if (spinner && noProducts) {
        // 30초 후 타임아웃 메시지 표시
        setTimeout(() => {
            if (spinner.style.display !== 'none') {
                showTimeoutMessage();
            }
        }, 30000);
    }
}

/**
 * 타임아웃 메시지 표시
 */
function showTimeoutMessage() {
    const noProducts = document.querySelector('.no-products');
    if (noProducts) {
        noProducts.innerHTML = `
            <h3>⏰ 분석 시간이 오래 걸리고 있습니다</h3>
            <p>AI 서버가 많은 요청을 처리하고 있어 시간이 소요되고 있습니다.</p>
            <p><small>잠시 후 다시 시도해보시거나, 다른 퍼스널 컬러로 시도해보세요.</small></p>
            <br>
        `;
    }
}

/**
 * 상품 카드 호버 효과 개선
 */
function enhanceProductCardEffects() {
    document.querySelectorAll('.product-card').forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-12px)';
            this.style.boxShadow = '0 20px 40px rgba(0,0,0,0.15)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '0 8px 25px rgba(0,0,0,0.1)';
        });
    });
}

/**
 * 스크롤 애니메이션 (선택사항)
 */
function handleScrollAnimations() {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    });

    document.querySelectorAll('.product-card').forEach(card => {
        observer.observe(card);
    });
}

/**
 * 에러 핸들링
 */
function handleImageErrors() {
    document.querySelectorAll('.product-image').forEach(img => {
        img.addEventListener('error', function() {
            this.style.backgroundColor = '#f8f9fa';
            this.style.display = 'flex';
            this.style.alignItems = 'center';
            this.style.justifyContent = 'center';
            this.style.color = '#6c757d';
            this.style.fontSize = '14px';
            this.alt = '이미지를 불러올 수 없습니다';
        });
    });
}

/**
 * 유틸리티 함수들
 */
const Utils = {
    // 디바운스 함수
    debounce: function(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },

    // 로컬 스토리지 안전 사용
    setLocalStorage: function(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
        } catch (e) {
            console.warn('로컬 스토리지 저장 실패:', e);
        }
    },

    getLocalStorage: function(key) {
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
    // 필요한 정리 작업 수행
    console.log('페이지 언로드 중...');
});