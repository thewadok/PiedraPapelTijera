package com.kotliners.piedrapapeltijera.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.ui.theme.AmarilloNeon

//Encabezado de marca
@Composable
fun BrandBar() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(start = 0.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_prov_kotliners),
            contentDescription = "Logo Kotliners",
            modifier = Modifier.height(64.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = stringResource(R.string.app_name),
            color = AmarilloNeon,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        )
    }
}