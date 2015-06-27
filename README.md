# MetroGatheringData
App for gathering GSM data in Warsaw metro system for later purposes.

Wyświetlanie sąsiednich nadajników podczas dodawania stacji działa tylko na pobłogosławionych telefonach.
Na Samsungu raczej nie zadziała, podobno działa między innymi na Desire HD, Nexus One.


Testy Instrumentalne
----------------------------------

1. Konfiguracja
Run -> Edit Configuration ... -> Android Tests -> Specific Instrumentation Runner -> AndroidJUnitRunner

2. Przygotowanie
Uruchom AVD

3. Uruchomienie
Terminal -> wpisz:

gradlew.bat connectedAndroidTest

4. Wyniki
app -> build -> reports -> androidTests -> index.html