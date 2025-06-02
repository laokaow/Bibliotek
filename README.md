Biblioteksapplikation för kursen JAVA II.
Javaapplikation som är kopplad till en databas.
Söka, Låna, Logga in, Registrera.
JavaFX för GUI.

För att köra finns två alternativ.
Alternativ ett är ett starta en ny run configuration där nedanstående sätts som
--module-path "rätt filsökväg här"\openjfx-17.0.15_windows-x64_bin-sdk\javafx-sdk-17.0.15\lib" --add-modules javafx.controls,javafx.fxml
Detta måste först laddas hem från Gluon.

Alternativ två är att ladda alla dependencies som hämtats från maven i pom.xml.
Då går det att köra i terminalen med mvn clean javafx:run

SDK 17 har använts i det här projektet.

Vissa funktioner är att önska och GUIn är inte helt användarvänlig.
Exempelvis så visas inte inloggad användare om än de är inloggade.
Logiken funkar men det är en bara en visuell(men väldigt viktig) grej.
Denna funktionalitet kommer att skapas vid ett senare tillfälle.

@authors Edwin Bylander & William Karlsson 
