import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.bcntransit.app.R

sealed class OnboardingPage(
    @StringRes val title: Int,
    @StringRes val description: Int,
    val icon: ImageVector
) {
    object First : OnboardingPage(
        title = R.string.onboarding_title_1,
        description = R.string.onboarding_desc_1,
        icon = Icons.Default.DirectionsBus
    )
    object Second : OnboardingPage(
        title = R.string.onboarding_title_2,
        description = R.string.onboarding_desc_2,
        icon = Icons.Default.Map
    )
    object Third : OnboardingPage(
        title = R.string.onboarding_title_3,
        description = R.string.onboarding_desc_3,
        icon = Icons.Default.PedalBike
    )
    object Fourth : OnboardingPage(
        title = R.string.onboarding_title_4,
        description = R.string.onboarding_desc_4,
        icon = Icons.Default.NearMe
    )
}