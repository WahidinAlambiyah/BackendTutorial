package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.TrxCoupon;
import com.mycompany.myapp.service.dto.TrxCouponDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrxCoupon} and its DTO {@link TrxCouponDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrxCouponMapper extends EntityMapper<TrxCouponDTO, TrxCoupon> {}
