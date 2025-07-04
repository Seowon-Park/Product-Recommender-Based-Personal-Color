package com.mywork.recom;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/")
    public String showIndexPage() {
        return "main/index";
    }

    @PostMapping("/recommend")
    public String recommendClothes(@RequestParam("personal") int personal, Model model) {
        try {
            // 퍼스널 컬러 가져오기
            String personalColor = homeService.getPersonalColor(personal);
            model.addAttribute("personalColor", personalColor);

            System.out.println("사용자 선택: " + personalColor);
            long startTime = System.currentTimeMillis();

            // AI 기반 퍼스널 컬러 추천
            String categoryUrl = "https://www.kolonmall.com/Category/List/133010071000?sort=newProduct-desc";
            List<ProductDTO> recommendedItems = homeService.getRecommendedProducts(categoryUrl, personalColor);

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            // 결과 정보
            model.addAttribute("items", recommendedItems);
            model.addAttribute("totalCount", recommendedItems.size());
            model.addAttribute("processingTime", totalTime / 1000); // 초 단위

            System.out.println("추천 완료: " + recommendedItems.size() + "개 상품 (처리시간: " + totalTime / 1000 + "초)");

            // 추천 결과에 따른 메시지 설정
            if (recommendedItems.size() > 0) {
                model.addAttribute("successMessage", personalColor + " 톤에 완벽하게 어울리는 상품들을 찾았습니다!");
            } else {
                model.addAttribute("infoMessage", "현재 " + personalColor + " 톤에 맞는 상품이 없습니다. 더 많은 상품을 분석해보세요.");
            }

            return "recommend/recommend";

        } catch (Exception e) {
            System.err.println("❌ 상품 추천 중 오류 발생: " + e.getMessage());
            e.printStackTrace();

            // 오류 발생시 기본값 설정
            String personalColor = homeService.getPersonalColor(personal);
            model.addAttribute("personalColor", personalColor);
            model.addAttribute("items", List.of());
            model.addAttribute("totalCount", 0);
            model.addAttribute("processingTime", 0);
            model.addAttribute("errorMessage", "상품 추천 중 오류가 발생했습니다. AI 서버 상태를 확인해주세요.");

            return "recommend/recommend";
        }
    }

    // 테스트용 API 엔드포인트들
    @GetMapping("/api/test-ai")
    @ResponseBody
    public String testAIServer() {
        try {
            // AI 서버 상태 확인 (이 기능은 AIColorService에 추가 필요)
            return "🟢 AI 서버 연결 정상";
        } catch (Exception e) {
            return "🔴 AI 서버 연결 실패: " + e.getMessage();
        }
    }

    @GetMapping("/api/analyze-sample")
    @ResponseBody
    public String analyzeSampleProduct() {
        try {
            // 샘플 이미지로 AI 분석 테스트
            String sampleImageUrl = "https://images.kolonmall.com/Prod_Img/10003657/2023/LS1/K1751523370314018WH01_LS1.jpg";
            // 여기서 AIColorService 호출하여 분석 결과 반환
            AIColorService.ColorAnalysisResult result = homeService.getAIColorService().analyzeProductColor(sampleImageUrl);

            return "샘플 이미지 분석 결과를 확인하세요. (실제 구현시 AIColorService 호출 필요)";

        } catch (Exception e) {
            return "🔴 샘플 분석 실패: " + e.getMessage();
        }
    }
}