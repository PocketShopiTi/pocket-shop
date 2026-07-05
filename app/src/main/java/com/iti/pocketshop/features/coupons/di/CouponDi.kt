package com.iti.pocketshop.features.coupons.di

import com.iti.pocketshop.features.coupons.data.datasource.CouponDataSource
import com.iti.pocketshop.features.coupons.data.datasource.CouponDataSourceImpl
import com.iti.pocketshop.features.coupons.data.repo.CouponRepositoryImpl
import com.iti.pocketshop.features.coupons.domain.repo.CouponRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CouponDi {

    @Binds
    abstract fun bindCouponRepository(
        couponRepositoryImpl: CouponRepositoryImpl
    ): CouponRepository

    @Binds
    abstract fun bindCouponDataSource(
        couponDataSourceImpl: CouponDataSourceImpl,
    ): CouponDataSource
}
