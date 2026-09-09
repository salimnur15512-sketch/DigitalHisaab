मस्जिद डिजिटल हिसाब — Android App Project

इस प्रोजेक्ट में पूरा HTML ऐप Android के अंदर assets/index.html के रूप में रखा गया है।
ऐप खुलते ही सीधे Digital Hisaab खुलेगा। पुराने /web/index.html वाले builder पर निर्भर नहीं है।

APK बनाने के लिए project को Android Studio/Android IDE या GitHub Actions में build करें।
GitHub Actions workflow: .github/workflows/build-apk.yml
Workflow को Actions → Build Digital Hisaab APK → Run workflow से चलाया जा सकता है।
Build के बाद Digital-Hisaab-APK artifact में app-debug.apk मिलेगा।

Demo login: admin / 1234
