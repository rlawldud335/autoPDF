package org.example;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--remote-allow-origins=*");

        ChromeDriver driver = new ChromeDriver(chromeOptions);
        int pagenum = 1;

        try {
            // gitbook start URL 설정
            String currentURL = args[0];
            String endURL = args[1];
            List<String> pdfFiles = new ArrayList<>();

            // 웹 페이지 열기
            driver.get(currentURL);

            // URL 랜더링 될 때까지 2초 대기
            Thread.sleep(2500);

            // header, nav 태그 삭제
            driver.executeScript("document.querySelector('header').remove();");
            driver.executeScript("document.querySelector('.css-175oi2r.r-k200y.r-14lw9ot.r-18ayb63.r-13l2t4g.r-12dqhl9.r-1rnoaur.r-gtdqiz.r-1fo40xd.r-18u37iz.r-17s6mgv').remove();");

            // endURL 까지 반복
            while(!endURL.equals(currentURL)){

                // expendable block 펼치기
                driver.executeScript(
                        "document.querySelectorAll('.css-175oi2r.r-156hn8l.r-kdyh1x.r-rs99b7.r-2zpn8w').forEach(function(element) {" +
                                "  var section = element.querySelector('section');" +
                                "  if (section) {" +
                                "    section.className = 'css-175oi2r r-nsbfu8 r-1fd96xs r-wk8lta';" +
                                "  }" +
                                "});"
                );

                // footer 삭제
                driver.executeScript(
                        "var elements = document.querySelectorAll('.view_manYY.blockWrapper_8BIg7.vertical0_jPhI0.horizontalAuto_xck7M.top600_q8Ng4');" +
                                "elements.forEach(function(element) {" +
                                "  element.style.display = 'none';" +
                                "});"
                );

                // PDF저장 옵션
                Map<String, Object> commandParams = new HashMap<>();
                commandParams.put("landscape", false); // 세로 방향
                commandParams.put("marginType", 1); // 여백 없음
                commandParams.put("scale", 0.7); // 배율 70%
                commandParams.put("printBackground", true); // 배경색 프린트

                // PDF 데이터 추출
                Map<String, Object> result = driver.executeCdpCommand("Page.printToPDF", commandParams);
                String base64EncodedPdf = (String) result.get("data");
                byte[] pdfData = Base64.getDecoder().decode(base64EncodedPdf);

                // 타이틀 가져오기
                WebElement title = driver.findElement(By.cssSelector(".r-1xnzce8.r-crgep1.r-37p410"));
                String text = title.getText();

                // PDF 파일로 저장
                String filename = "page"+(pagenum++)+"_" + text + ".pdf";
                Files.write(Paths.get(filename), pdfData);
                pdfFiles.add(filename);

                // footer 복구
                driver.executeScript(
                        "var elements = document.querySelectorAll('.view_manYY.blockWrapper_8BIg7.vertical0_jPhI0.horizontalAuto_xck7M.top600_q8Ng4');" +
                                "elements.forEach(function(element) {" +
                                "  element.style.display = '';" +
                                "});"
                );

                // 다음 페이지 버튼 클릭
                WebElement element = driver.findElement(By.cssSelector(".css-175oi2r.r-1peese0"));
                if(element.findElements(By.xpath("./*")).size() == 1){
                    element.findElements(By.xpath("./*")).get(0).click();
                }else{
                    element.findElements(By.xpath("./*")).get(1).click();
                }

                // URL 랜더링 될 때까지 2초 대기
                Thread.sleep(2000);

                // currentURL 변수 업데이트
                currentURL = driver.getCurrentUrl();
            }

            // 하나의 PDF로 병합하기
            PDFMergerUtility mergerUtility = new PDFMergerUtility();
            mergerUtility.setDestinationFileName("gitbook.pdf");

            for(String pdfFile : pdfFiles){
                mergerUtility.addSource(new File(pdfFile));
            }

            mergerUtility.mergeDocuments(null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 브라우저 닫기
            driver.quit();
        }
    }
}
