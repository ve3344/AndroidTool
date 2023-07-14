package me.lwb.androidtool.ui.page

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import compose.icons.FeatherIcons
import compose.icons.feathericons.PhoneIncoming
import me.lwb.androidtool.base.rememberViewModel
import me.lwb.androidtool.data.bean.MdnsService
import me.lwb.androidtool.library.qrcode.QrCode
import me.lwb.androidtool.library.qrcode.QrCodePainter
import me.lwb.androidtool.ui.theme.ThemeColors
import me.lwb.androidtool.ui.widget.CommonPopupDialog
import me.lwb.androidtool.vm.PairViewModel

/**
 * Created by ve3344 .
 */
@Composable
fun ConnectDevicePage(deviceViewModel: PairViewModel = rememberViewModel()) {

    val pairPageVisible by deviceViewModel.pairPageVisible.collectAsState()
    CommonPopupDialog(
        pairPageVisible,
        { deviceViewModel.hidePairPage() }, modifier = Modifier.wrapContentSize(), title = "添加设备"
    ) {
        val codePairingServices by deviceViewModel.codePairingServices.collectAsState()


        LazyColumn(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    var address by remember { mutableStateOf("") }
                    TextField(
                        address,
                        onValueChange = { address = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("ip:port") })
                    Button({
                        deviceViewModel.connect(address)
                    }) {
                        Text("链接")
                    }

                }
            }
            item {
                Divider(Modifier.height(1.dp), ThemeColors.GreyBackground)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    var address by remember { mutableStateOf("") }
                    TextField(
                        address,
                        onValueChange = { address = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("ip:port") })

                    var code by remember { mutableStateOf("") }
                    TextField(
                        code,
                        onValueChange = { code = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("code") })

                    Button({
                        deviceViewModel.pair(address, code)
                    }) {
                        Text("配对")
                    }
                }
            }


            item {
                Divider(Modifier.height(1.dp), ThemeColors.GreyBackground)
            }

            item {
                QrCodePage(deviceViewModel)
            }
            item {
                Divider(Modifier.height(1.dp), ThemeColors.GreyBackground)
                Spacer(Modifier.height(10.dp))

            }

            ServicePage(deviceViewModel, codePairingServices)


            item {
                Spacer(Modifier.height(10.dp))
            }
        }


    }


}

private fun LazyListScope.ServicePage(
    deviceViewModel: PairViewModel,
    codePairingServices: List<MdnsService>,
) {

    itemsIndexed(codePairingServices) { index, item ->
        ServiceItem(index, item, {
            deviceViewModel.pair(item.address, it)
        }, {
            deviceViewModel.connect(item.address)
        })
    }
}

@Composable
private fun QrCodePage(deviceViewModel: PairViewModel) {
    val scanPairStatus by deviceViewModel.scanPairStatus.collectAsState()
    val scanPairStatusLocal = scanPairStatus ?: return
    when (scanPairStatusLocal) {
        is PairViewModel.ScanPairStatus.WaitScan -> {
            val painter = remember(scanPairStatusLocal.qrCode) {
                QrCodePainter(
                    QrCode.encode(scanPairStatusLocal.qrCode),
                    cornerRadius = CornerRadius(1f, 1f),
                    bitColor = ThemeColors.ThemeMain
                )
            }
            val interactionSource = remember { MutableInteractionSource() }
            val hover by interactionSource.collectIsHoveredAsState()

            val scale by animateFloatAsState(if (hover) 1.5f else 1f)

            Box(Modifier.fillMaxWidth().zIndex(scale), contentAlignment = Alignment.Center) {
                Image(painter, "", Modifier.size(150.dp)
                    .scale(scale)
                    .background(ThemeColors.GreyBackground, RoundedCornerShape(4.dp))
                    .hoverable(interactionSource,true))
            }
        }
        is PairViewModel.ScanPairStatus.WaitDevice -> {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(FeatherIcons.PhoneIncoming, "")
            }
        }

    }

}

@Composable
private fun ServiceItem(
    index: Int,
    item: MdnsService,
    onPair: (String) -> Unit,
    onConnect: () -> Unit,
) {

    val colors = arrayOf(ThemeColors.tableBackground1, ThemeColors.tableBackground2)
    Row(Modifier.clickable { }
        .background(colors[index % 2])
        .padding(top = 2.dp, bottom = 2.dp)

    ) {
        Text(
            item.serviceName + " " + item.serviceType,
            Modifier.weight(1f).padding(start = 10.dp),
            fontSize = 13.sp
        )
        Text(
            item.address,
            Modifier.weight(1f).padding(end = 10.dp),
            textAlign = TextAlign.End, fontSize = 13.sp
        )
        if (item.serviceType == MdnsService.ADB_PAIR) {

            val source = remember { MutableInteractionSource() }
            var inputValue: String by remember { mutableStateOf("") }

            val focus by source.collectIsFocusedAsState()
            OutlinedTextField(
                inputValue, { inputValue = it },
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(2.dp)
                    .border(
                        if (focus) 2.dp else 1.dp,
                        if (focus) ThemeColors.ThemeMain else ThemeColors.GreyText,
                        RoundedCornerShape(2.dp)
                    )
                    .align(Alignment.CenterVertically),
                interactionSource = source
            )
            Button({
                onPair(inputValue)
            }) {
                Text("配对")
            }
        } else {
            Button({
                onConnect()
            }) {
                Text("链接")
            }
        }

    }
}