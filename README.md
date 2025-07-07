### project[Spring/Flask/OpenAI vision API] 
2023.05.18 -
# (vision AI 활용)상품 분류 및 사용자 특성 맞춤 상품 추천 웹페이지

### 소개
1. 2023년도에 개제한 AI응용 논문의 아이디어를 바탕으로 퍼스널 컬러 기반 상품 추천 웹페이지를 제작한다.<br/>
<a href="https://www.koreascience.kr/article/CFKO202319360803443.page?&lang=ko">[딥러닝 기반 의류원단 염색을 통한 개인 맞춤형 의상 제작시스템 설계]<br/>
2. 웹 어플리케이션은 Spring 프레임워크를 기반으로 구현한다.<br/>
3. 상품의 색상은 PCCS표색계를 기준으로 퍼스널컬러 분류군에 따라 분류한다. <br/>
4. 상품 이미지의 색상 추출 및 분류 작업은 OpenAI vision API를 활용하며, 이를 통해 얻은 데이터를 웹페이지에 맞게 가공하여 활용한다.<br/> 

### 주요기능
1. PCCS 표색계 기반 분류:<br/>
상품 이미지를 분석하여 색상 정보를 추출하고, 이를 PCCS 기반 퍼스널 컬러 분류군에 따라 정제 및 매핑<br/>
2. AI 이미지 분석 연동:<br/>
Flask 서버에서 OpenAI Vision API를 통해 상품 이미지의 주요 색상 값을 추출 및 분류. 해당 정보를 Spring 서버로 전달하여 추천 로직에 활용<br/>
3. 맞춤형 상품 추천:<br/>
사용자의 퍼스널 컬러 타입(예: 봄 웜, 겨울 쿨 등)에 따라 어울리는 상품을 자동 추천<br/>

### 제작 목적
논문에서 제안한 아이디어를 바탕으로, 실제 AI 분류 로직을 구현하고 시스템으로 구체화함<br/>
(단, 실제 제조 공정 데이터를 확보하기 어려운 현실적 한계로 상품 이미지를 대체 수단으로 활용하여 색상 데이터 분석 방식을 적용함)<br/>

### 기술스택
● 백엔드 프레임워크: Spring (Tomcat 9 / javax.servlet 기반)<br/>
● JDK 버전: JDK 21<br/>
● AI 서버: Flask(Python 기반)<br/>
● AI 모델 및 이미지 처리: OpenAI Vision API<br/>

### 블로그 기록
<a href="https://codetails.tistory.com/21">[퍼스널 컬러 기반 상품 추천 웹페이지 | Spring, AI서버(Flask), AI모델(OpenAI Vision API)]<br/>
