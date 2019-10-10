package com.farsheel.mypos.di

import android.content.Context
import android.content.SharedPreferences
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.repository.*
import com.farsheel.mypos.view.cart.CartViewModel
import com.farsheel.mypos.view.category.list.CategoryListViewModel
import com.farsheel.mypos.view.category.view.AddEditCategoryViewModel
import com.farsheel.mypos.view.home.HomeViewModel
import com.farsheel.mypos.view.login.LoginViewModel
import com.farsheel.mypos.view.order.OrderHistoryViewModel
import com.farsheel.mypos.view.payment.airtel.AirtelPaymentViewModel
import com.farsheel.mypos.view.payment.cash.CashPaymentViewModel
import com.farsheel.mypos.view.payment.completed.PaymentCompletedViewModel
import com.farsheel.mypos.view.payment.gmoney.GmoneyPaymentViewModel
import com.farsheel.mypos.view.payment.masterpass.MasterpassPaymentViewModel
import com.farsheel.mypos.view.payment.mtn.MtnPaymentViewModel
import com.farsheel.mypos.view.payment.tender.PaymentTenderViewModel
import com.farsheel.mypos.view.payment.visa.VisaPaymentViewModel
import com.farsheel.mypos.view.payment.vodafone.VodafonePaymentViewModel
import com.farsheel.mypos.view.product.list.ProductListViewModel
import com.farsheel.mypos.view.product.view.AddEditProductViewModel
import com.farsheel.mypos.view.qr.QrViewModel
import com.farsheel.mypos.view.splash.SplashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val storageModule = module(override = true) {
    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            "SharedPreferences",
            Context.MODE_PRIVATE
        )
    }
    single {
        AppDatabase.invoke(androidContext())
    }
}

val repositoryModule = module(override = true) {
    single {
        AuthRepository(get())
    }
    single {
        CartRepository(get(), get())
    }
    single {
        CategoryRepository(get(), get())
    }
    single {
        ProductRepository(get(), get())
    }
    single {
        OrderRepository(get(), get())
    }
    single {
        ReceiptRepository(get(), get())
    }
    single {
        WorkRepository(get())
    }
}

val viewModelModule = module {
    viewModel { CartViewModel(get()) }
    viewModel { QrViewModel(get(), get()) }
    viewModel { CategoryListViewModel(get()) }
    viewModel { AddEditCategoryViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { OrderHistoryViewModel(get()) }
    viewModel { CashPaymentViewModel(get(), get()) }
    viewModel { PaymentCompletedViewModel(get()) }
    viewModel { PaymentTenderViewModel(get()) }
    viewModel { ProductListViewModel(get()) }
    viewModel { AddEditProductViewModel(get(), get()) }
    viewModel { AirtelPaymentViewModel(get(), get()) }
    viewModel { GmoneyPaymentViewModel(get(), get()) }
    viewModel { MasterpassPaymentViewModel(get(), get()) }
    viewModel { MtnPaymentViewModel(get(), get()) }
    viewModel { VisaPaymentViewModel(get(), get()) }
    viewModel { VodafonePaymentViewModel(get(), get()) }
    viewModel { SplashViewModel(get()) }
}