
package com.mywork.recom;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class HomeService {

    @Autowired
    private AIColorService aiColorService;

    // 테스트용 상품 개수 제한
    private static final int TEST_PRODUCT_LIMIT = 6;

    public String getPersonalColor(int personal) {
        String[] color = {"", "봄 라이트", "봄 브라이트", "여름 라이트", "여름 브라이트", "여름 뮤트", "가을 뮤트", "가을 스트롱", "가을 딥", "겨울 브라이트", "겨울 딥"};
        return color[personal];
    }

    /**
     * 퍼스널 컬러 기반 상품 추천 (디버깅 강화)
     */
    public List<ProductDTO> getRecommendedProducts(String url, String userPersonalColor) {
        System.out.println("=== 상품 추천 시작 ===");
        System.out.println("사용자 퍼스널 컬러: " + userPersonalColor);
        System.out.println("테스트 모드: 최신 상품 " + TEST_PRODUCT_LIMIT + "개 분석");

        // AI 서버 상태 확인
        boolean aiServerHealthy = aiColorService.isAIServerHealthy();
        System.out.println("AI 서버 상태: " + (aiServerHealthy ? "정상" : "비정상"));

        if (!aiServerHealthy) {
            System.err.println("AI 서버가 응답하지 않습니다. 기본 상품 리스트를 반환합니다.");
            return getProductListLimited(url, TEST_PRODUCT_LIMIT);
        }

        long startTime = System.currentTimeMillis();

        // 1단계: 최신 상품 수집
        System.out.println("\n=== 1단계: 상품 수집 ===");
        List<ProductDTO> limitedProducts = getProductListLimited(url, TEST_PRODUCT_LIMIT);
        System.out.println("수집된 상품 수: " + limitedProducts.size());

        if (limitedProducts.isEmpty()) {
            System.err.println("상품 데이터를 가져올 수 없습니다.");
            return limitedProducts;
        }

        // 수집된 상품 목록 출력
        System.out.println("\n=== 수집된 상품 목록 ===");
        for (int i = 0; i < limitedProducts.size(); i++) {
            ProductDTO product = limitedProducts.get(i);
            System.out.println((i + 1) + ". " + product.getName());
            System.out.println("   이미지: " + product.getImageUrl());
        }

        // 2단계: AI 색상 분석 및 필터링
        System.out.println("\n=== 2단계: AI 색상 분석 ===");
        List<ProductDTO> recommendedProducts = new ArrayList<>();

        int analyzedCount = 0;
        int matchedCount = 0;

        for (ProductDTO product : limitedProducts) {
            try {
                System.out.println("\n--- 분석 중 [" + (analyzedCount + 1) + "/" + limitedProducts.size() + "] ---");
                System.out.println("상품명: " + product.getName());
                System.out.println("이미지 URL: " + product.getImageUrl());

                // AI 색상 분석
                AIColorService.ColorAnalysisResult analysis = aiColorService.analyzeProductColor(product.getImageUrl());
                analyzedCount++;

                System.out.println("AI 분석 결과:");
                System.out.println("  - 퍼스널 컬러: " + analysis.getPersonalColor());
                System.out.println("  - 신뢰도: " + analysis.getConfidence() + "%");
                System.out.println("  - 분석 근거: " + analysis.getReason());

                // 디버깅: 호환성 확인 과정
                System.out.println("\n호환성 확인:");
                System.out.println("  - 사용자 컬러: " + userPersonalColor);
                System.out.println("  - 상품 컬러: " + analysis.getPersonalColor());
                System.out.println("  - 신뢰도: " + analysis.getConfidence() + "%");

                boolean isCompatible = aiColorService.isCompatibleColor(userPersonalColor, analysis.getPersonalColor(), analysis.getConfidence());
                System.out.println("  - 호환성 결과: " + (isCompatible ? "매칭됨" : "매칭 안됨"));

                if (isCompatible) {
                    recommendedProducts.add(product);
                    matchedCount++;
                    System.out.println("✅ 매칭 성공!");
                } else {
                    System.out.println("❌ 매칭 실패");

                    // 매칭 실패 이유 상세 분석
                    debugMatchingFailure(userPersonalColor, analysis);
                }

                // 짧은 대기 시간
                Thread.sleep(500);

            } catch (Exception e) {
                System.err.println("상품 분석 실패: " + product.getName() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 최종 결과 출력
        System.out.println("\n=== 최종 결과 ===");
        System.out.println("테스트 상품: " + limitedProducts.size() + "개");
        System.out.println("분석 완료: " + analyzedCount + "개");
        System.out.println("매칭된 상품: " + matchedCount + "개");
        System.out.println("처리 시간: " + (duration / 1000) + "초");
        if (analyzedCount > 0) {
            System.out.println("매칭률: " + String.format("%.1f", (double) matchedCount / analyzedCount * 100) + "%");
        }

        if (recommendedProducts.isEmpty()) {
            System.out.println("\n⚠️ 매칭된 상품이 없습니다!");
        } else {
            System.out.println("\n✅ 추천 상품 목록:");
            for (int i = 0; i < recommendedProducts.size(); i++) {
                System.out.println((i + 1) + ". " + recommendedProducts.get(i).getName());
            }
        }

        return recommendedProducts;
    }

    /**
     * 매칭 실패 이유 상세 분석
     */
    private void debugMatchingFailure(String userPersonalColor, AIColorService.ColorAnalysisResult analysis) {
        System.out.println("\n--- 매칭 실패 상세 분석 ---");

        // 신뢰도 체크
        if (analysis.getConfidence() < 60) {
            System.out.println("실패 이유: 신뢰도 부족 (" + analysis.getConfidence() + "% < 60%)");
            return;
        }

        // 정확한 일치 체크
        if (userPersonalColor.equals(analysis.getPersonalColor())) {
            System.out.println("이상함: 정확히 일치하는데 매칭 실패");
            return;
        }

        // 계절별 호환성 체크
        String userSeason = extractSeason(userPersonalColor);
        String productSeason = extractSeason(analysis.getPersonalColor());
        System.out.println("사용자 계절: " + userSeason);
        System.out.println("상품 계절: " + productSeason);

        if (userSeason.equals(productSeason)) {
            System.out.println("이상함: 같은 계절인데 매칭 실패");
            return;
        }

        // 보색 관계 체크
        boolean isComplementary = (userSeason.equals("봄") && productSeason.equals("가을")) ||
                (userSeason.equals("가을") && productSeason.equals("봄")) ||
                (userSeason.equals("여름") && productSeason.equals("겨울")) ||
                (userSeason.equals("겨울") && productSeason.equals("여름"));

        if (isComplementary) {
            System.out.println("보색 관계이지만 신뢰도 부족 (" + analysis.getConfidence() + "% <= 80%)");
        } else {
            System.out.println("다른 계열의 색상이므로 매칭 제외");
        }
    }

    /**
     * 계절 추출 (디버깅용)
     */
    private String extractSeason(String personalColor) {
        if (personalColor.startsWith("봄")) return "봄";
        if (personalColor.startsWith("여름")) return "여름";
        if (personalColor.startsWith("가을")) return "가을";
        if (personalColor.startsWith("겨울")) return "겨울";
        return "알 수 없음";
    }

    /**
     * AIColorService 접근 메서드
     */
    public AIColorService getAIColorService() {
        return aiColorService;
    }

    /**
     * 제한된 개수의 상품만 스크래핑 (로깅 개선)
     */
    public static List<ProductDTO> getProductListLimited(String url, int limit) {
        System.out.println("제한된 상품 스크래핑 시작 (최대 " + limit + "개)");

        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        List<ProductDTO> products = new ArrayList<>();

        try {
            driver.get(url);

            // 로딩 메시지
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.body.insertAdjacentHTML('afterbegin', '<div id=\"loadingMsg\" style=\"position:fixed;top:20px;left:20px;padding:10px;background:#000;color:#fff;z-index:9999;border-radius:5px\">최신 상품 " + limit + "개 수집 중...</div>');");

            // 팝업 닫기
            try {
                WebDriverWait popupWait = new WebDriverWait(driver, Duration.ofSeconds(3));
                WebElement closeBtn = popupWait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".popup-close")));
                closeBtn.click();
            } catch (Exception ignored) {
            }

            // 상품 영역 대기
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href*='/Product/']")));

            List<WebElement> productElements = driver.findElements(By.cssSelector("a[href*='/Product/']"));
            System.out.println("전체 상품 요소 수: " + productElements.size());

            // 제한된 개수만 처리
            int processedCount = 0;
            for (WebElement el : productElements) {
                if (processedCount >= limit) {
                    System.out.println("제한된 개수 도달: " + limit + "개");
                    break;
                }

                // 상품 정보 추출
                String name = "";
                try {
                    name = el.findElement(By.cssSelector("div.textStyle_Body-14-M")).getText();
                } catch (Exception ignored) {
                }

                String imageUrl = "";
                try {
                    WebElement imgEl = el.findElement(By.cssSelector("img"));
                    imageUrl = imgEl.getAttribute("src");
                    if (imageUrl == null || imageUrl.isEmpty()) {
                        imageUrl = imgEl.getAttribute("data-src");
                    }
                } catch (Exception ignored) {
                }

                String productLink = el.getAttribute("href");
                if (!productLink.startsWith("http")) {
                    productLink = "https://www.kolonmall.com" + productLink;
                }

                if (!name.isEmpty() && !imageUrl.isEmpty()) {
                    products.add(new ProductDTO(name, imageUrl, productLink));
                    processedCount++;
                    System.out.println("상품 추가 [" + processedCount + "/" + limit + "]: " + name);
                }
            }

            // 메시지 제거
            js.executeScript("const msg = document.getElementById('loadingMsg'); if (msg) msg.remove();");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        System.out.println("최종 수집된 상품 수: " + products.size());
        return products;
    }
}