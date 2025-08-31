package com.ryen.sunnah_alhadi.presentation.screens.preferences

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight

object PreferenceContent {

    val aboutContent = """

Sunnah Al-Hadi is a comprehensive collection of Sunnahs from the life of Prophet Muhammad ﷺ, designed to help Muslims incorporate these blessed practices into their daily lives.

**Features:**
• 470+ Sunnahs and manners with references
• Categorized by daily activities and occasions  
• Arabic text with English translations
• Bookmark your favorite Sunnahs
• Daily Sunnah reminders
• Fully offline - no internet required

**Sources:**
The Sunnahs in this app are compiled from authentic hadith collections — including Sahih al-Bukhari, Sahih Muslim, Sunan Abu Dawood, Jami‘ al-Tirmidhi — as well as classical Islamic works of fiqh and guidance such as Bahar-e-Shariat, Fatawa Alamgiri, Fatawa Razawiya, and Ihya’ ‘Ulum al-Din, among other reliable references.

**Our Mission:**
To make the beautiful Sunnah of our beloved Prophet ﷺ easily accessible to Muslims worldwide, helping them follow his guidance in their daily lives.

May Allah ﷻ accept this effort and make it a source of continuous reward (Sadaqah Jariyah) for all involved.

**Developer:**
This app is developed with love and dedication to serve the Muslim Ummah. 

**Disclaimer:**
While we strive for accuracy, please consult Islamic scholars for religious guidance on important matters.

جزاكم الله خيراً
    """.trimIndent()

    val privacyPolicyContent = """
**PRIVACY POLICY FOR SUNNAH AL-HADI**

**Last Updated:** August 2025


**Data Collection:**
Sunnah Al-Hadi is designed with privacy in mind:

• **No Personal Data Collection**: We do not collect, store, or transmit any personal information
• **Offline First**: All Sunnah content is stored locally on your device
• **No User Accounts**: No registration or login required
• **Local Preferences**: Your settings and bookmarks are stored only on your device

**Crash Reporting:**
• We use Firebase Crashlytics to improve app stability
• Only technical crash data is collected (no personal content)
• No identification of individual users

**Notifications:**
• Local notifications only (no cloud messaging)
• Notification permissions used only for Sunnah reminders
• No data leaves your device

**Third-Party Services:**
• Google Play Services (for app distribution)
• Firebase Crashlytics (for crash reporting only)

**Children's Privacy:**
This app is suitable for all ages and does not collect data from children.

**Changes:**
We may update this policy and will notify users through app updates.

**Contact:**
For privacy concerns, contact us at developer@sunnahalhadi.com

**Islamic Principle:**
Following Islamic principles of privacy and trust (Amanah), we commit to protecting user privacy as a religious obligation.

والله أعلم
    """.trimIndent()

    val termsOfServiceContent = """
**TERMS OF SERVICE FOR SUNNAH AL-HADI**

**Last Updated:** January 2025

**بسم الله الرحمن الرحيم**

**Acceptance:**
By using Sunnah Al-Hadi, you agree to these terms.

**App Purpose:**
This app provides authentic Sunnahs from Prophet Muhammad ﷺ for educational and spiritual guidance.

**Content Disclaimer:**
• All content is compiled from authentic Islamic sources
• Users should consult qualified Islamic scholars for important religious decisions
• We strive for accuracy but are not responsible for interpretation differences
• Content is provided "as is" for educational purposes

**User Responsibilities:**
• Use the app respectfully and in accordance with Islamic principles
• Do not misuse or redistribute the content inappropriately
• Respect the sacred nature of the content

**Intellectual Property:**
• Hadith and Sunnah content are from public Islamic sources
• App design and compilation are our intellectual property
• You may share content for dawah (Islamic outreach) purposes

**Prohibited Uses:**
• Commercial use without permission
• Modifying or misrepresenting the content
• Using content in ways contrary to Islamic teachings

**Limitation of Liability:**
We provide this app as a service to the Muslim community. We are not liable for decisions made based solely on app content.

**Religious Guidance:**
For important religious matters, always consult qualified Islamic scholars in addition to using this app.

**Termination:**
We reserve the right to discontinue the app or remove it from app stores.

**Islamic Principles:**
These terms are governed by Islamic principles of justice (Adl) and good faith (Husn al-Qada).

**Contact:**
For questions about these terms, contact us at mdsarfraz.ilanos1915@gmail.com

May Allah ﷻ bless this effort and make it beneficial for the Ummah.

بارك الله فيكم
    """.trimIndent()
}

@Composable
fun MarkdownText(text: String, modifier: Modifier = Modifier) {
    val styled = buildAnnotatedString {
        val boldRegex = "\\*\\*(.*?)\\*\\*".toRegex()
        var lastIndex = 0

        boldRegex.findAll(text).forEach { match ->
            // Append text before the bold section
            append(text.substring(lastIndex, match.range.first))

            // Apply bold style
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append(match.groupValues[1])
            pop()

            lastIndex = match.range.last + 1
        }

        // Append remaining text
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }

    Text(
        text = styled,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.tertiary,
        modifier = modifier
    )
}