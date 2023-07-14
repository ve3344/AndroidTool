package me.lwb.androidtool.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.MenuDefaults
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Check
import compose.icons.feathericons.Link
import compose.icons.feathericons.Smartphone
import me.lwb.androidtool.base.rememberViewModel
import me.lwb.androidtool.data.bean.DeviceBean
import me.lwb.androidtool.ui.theme.ThemeColors
import me.lwb.androidtool.vm.DeviceViewModel
import me.lwb.androidtool.vm.PairViewModel


@Composable
fun BottomBar(
    viewModel: DeviceViewModel = rememberViewModel(),
) {

    var expandDevices by remember {
        mutableStateOf(false)
    }
    val devices by viewModel.devices.collectAsState()
    val currentDevice by viewModel.currentDevice.collectAsState()

    Column {
        DropdownMenu(
            expanded = expandDevices,
            onDismissRequest = { expandDevices = false },
            offset = DpOffset(10.dp, 10.dp),
        ) {
            devices.forEach {
                DeviceItem(it, it == currentDevice) {
                    viewModel.changeCurrent(it)
                    expandDevices = false
                }
            }
        }

        Row(Modifier.height(30.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier
                .background(ThemeColors.ThemeMain, shape = RoundedCornerShape(0.dp, 20.dp, 20.dp, 0.dp))
                .clickable {
                    viewModel.loadDevices()
                    expandDevices = true
                }
                .padding(6.dp)
                .wrapContentWidth()
            ) {
                Icon(imageVector = FeatherIcons.Smartphone,
                    contentDescription = "",
                    tint = ThemeColors.ThemeWhite, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(10.dp))
                Text(text = currentDevice?.model ?: "",
                    maxLines = 1,
                    fontSize = 13.sp,
                    color = ThemeColors.ThemeWhite)
            }

            val pairViewModel:PairViewModel = rememberViewModel()
            Spacer(Modifier.width(10.dp))

            Box (Modifier
                .size(24.dp)
                .border(1.dp,ThemeColors.ThemeMain, CircleShape)
                .clickable { pairViewModel.showPairPage() }, contentAlignment = Alignment.Center){
                Icon(imageVector = FeatherIcons.Link,
                    contentDescription = "",
                    modifier = Modifier.size(12.dp))
            }

        }
    }


}

@Composable
private fun DeviceItem(deviceBean: DeviceBean, selected: Boolean, onClick: () -> Unit) {

    Row(
        modifier = Modifier
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(true)
            )
            .padding(MenuDefaults.DropdownMenuItemContentPadding)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = FeatherIcons.Smartphone,
            contentDescription = "",
            tint = ThemeColors.GreyText)
        Spacer(Modifier.width(10.dp))
        Column {
            Text(text = deviceBean.model,
                color = ThemeColors.GreyText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(2.dp))

            Text(text = deviceBean.name + " | " + deviceBean.product + " | " + deviceBean.transportId,
                color = ThemeColors.GreyText, fontSize = 12.sp)
        }
        Spacer(Modifier.width(20.dp))
        Spacer(Modifier.weight(1f))

        Box() {
            if (selected) {
                Icon(FeatherIcons.Check, "")
            }
        }

    }

}
