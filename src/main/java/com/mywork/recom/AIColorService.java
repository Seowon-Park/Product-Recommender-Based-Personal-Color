package com.mywork.recom;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AIColorService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String AI_SERVER_URL = "http://localhost:8000";

    // 캐시 저장소 (성능 최적화용)
    private final Map<String, ColorAnalysisResult> cache = new HashMap<>();
    private final Map<String, Long> cacheTimestamps = new HashMap<>();
    private final long CACHE_DURATION = TimeUnit.MINUTES.toMillis(10); // 10분 캐시

    public AIColorService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 이미지 URL로 색상 분석 요청
     */
    public ColorAnalysisResult analyzeProductColor(String imageUrl) {
        // 캐시 확인
        ColorAnalysisResult cachedResult = getCachedResult(imageUrl);
        if (cachedResult != null) {
            System.out.println("캐시에서 결과 반환: " + imageUrl);
            return cachedResult;
        }

        try {
            // AI 서버 호출
            Map<String, String> requestData = new HashMap<>();
            requestData.put("image_url", imageUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestData, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    AI_SERVER_URL + "/analyze-color",
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                ColorAnalysisResult result = parseAIResponse(response.getBody());

                // 캐시에 저장
                saveToCache(imageUrl, result);

                System.out.println("AI 분석 완료: " + result.getPersonalColor()+ "신뢰도: " + result.getConfidence() + "%)");
                return result;
            } else {
                System.err.println("AI 서버 응답 오류: " + response.getStatusCode());
                return createDefaultResult();
            }

        } catch (RestClientException e) {
            System.err.println("AI 서버 연결 실패: " + e.getMessage());
            return createDefaultResult();
        } catch (Exception e) {
            System.err.println("AI 색상 분석 중 오류: " + e.getMessage());
            return createDefaultResult();
        }
    }

    /**
     * AI 응답 JSON 파싱
     */
    private ColorAnalysisResult parseAIResponse(String jsonResponse) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            String personalColor = jsonNode.has("personal_color")
                    ? jsonNode.get("personal_color").asText()
                    : "알 수 없음";

            int confidence = jsonNode.has("confidence")
                    ? jsonNode.get("confidence").asInt()
                    : 0;

            String reason = jsonNode.has("reason")
                    ? jsonNode.get("reason").asText()
                    : "분석 결과 없음";

            List<String> dominantColors = new ArrayList<>();
            if (jsonNode.has("dominant_colors")) {
                JsonNode colorsNode = jsonNode.get("dominant_colors");
                if (colorsNode.isArray()) {
                    colorsNode.forEach(color -> dominantColors.add(color.asText()));
                }
            }

            return new ColorAnalysisResult(personalColor, confidence, reason, dominantColors);

        } catch (Exception e) {
            System.err.println("AI 응답 파싱 오류: " + e.getMessage());
            return createDefaultResult();
        }
    }

    /**
     * 캐시에서 결과 조회
     */
    private ColorAnalysisResult getCachedResult(String imageUrl) {
        if (cache.containsKey(imageUrl)) {
            long timestamp = cacheTimestamps.get(imageUrl);
            if (System.currentTimeMillis() - timestamp < CACHE_DURATION) {
                return cache.get(imageUrl);
            } else {
                // 캐시 만료시 삭제
                cache.remove(imageUrl);
                cacheTimestamps.remove(imageUrl);
            }
        }
        return null;
    }

    /**
     * 캐시에 결과 저장
     */
    private void saveToCache(String imageUrl, ColorAnalysisResult result) {
        cache.put(imageUrl, result);
        cacheTimestamps.put(imageUrl, System.currentTimeMillis());
    }

    /**
     * 기본 결과 생성 (AI 분석 실패시)
     */
    private ColorAnalysisResult createDefaultResult() {
        return new ColorAnalysisResult("알 수 없음", 0, "AI 분석 실패", Arrays.asList("#000000"));
    }

    /**
     * 퍼스널 컬러 호환성 확인 (개선된 버전)
     */
    public boolean isCompatibleColor(String userPersonalColor, String productPersonalColor, int confidence) {
        System.out.println("    [호환성 체크] 시작");
        System.out.println("      사용자: " + userPersonalColor);
        System.out.println("      상품: " + productPersonalColor);
        System.out.println("      신뢰도: " + confidence + "%");

        // 1. 신뢰도가 너무 낮으면 제외 (40% 미만)
        if (confidence < 40) {
            System.out.println("      결과: 신뢰도 부족 (" + confidence + "% < 40%)");
            return false;
        }

        // 2. 정확히 일치하는 경우
        if (userPersonalColor.equals(productPersonalColor)) {
            System.out.println("      결과: 정확히 일치!");
            return true;
        }

        // 3. 계절별 호환성 확인
        String userSeason = extractSeason(userPersonalColor);
        String productSeason = extractSeason(productPersonalColor);

        System.out.println("      사용자 계절: " + userSeason);
        System.out.println("      상품 계절: " + productSeason);

        if (userSeason.equals(productSeason)) {
            System.out.println("      결과: 같은 계절 - 호환됨!");
            return true;
        }


        // 5. 신뢰도 40% 이상이면 매칭 허용
        if (confidence >= 40) {
            System.out.println("      결과: 신뢰도 40% 이상으로 매칭 허용");
            return true;
        }

        System.out.println("      결과: 호환되지 않음");
        return false;
    }

    /**
     * 퍼스널 컬러에서 계절 추출
     */
    private String extractSeason(String personalColor) {
        if (personalColor.startsWith("봄")) return "봄";
        if (personalColor.startsWith("여름")) return "여름";
        if (personalColor.startsWith("가을")) return "가을";
        if (personalColor.startsWith("겨울")) return "겨울";
        return "알 수 없음";
    }

    /**
     * AI 서버 상태 확인
     */
    public boolean isAIServerHealthy() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    AI_SERVER_URL + "/health",
                    String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("AI 서버 상태 확인 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * 색상 분석 결과 클래스
     */
    public static class ColorAnalysisResult {
        private String personalColor;
        private int confidence;
        private String reason;
        private List<String> dominantColors;

        public ColorAnalysisResult(String personalColor, int confidence, String reason, List<String> dominantColors) {
            this.personalColor = personalColor;
            this.confidence = confidence;
            this.reason = reason;
            this.dominantColors = dominantColors;
        }

        // Getters
        public String getPersonalColor() { return personalColor; }
        public int getConfidence() { return confidence; }
        public String getReason() { return reason; }
        public List<String> getDominantColors() { return dominantColors; }
    }
}