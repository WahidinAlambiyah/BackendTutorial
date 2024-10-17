package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstBrand;
import com.mycompany.myapp.domain.MstCategory;
import com.mycompany.myapp.domain.MstProduct;
import com.mycompany.myapp.domain.MstSupplier;
import com.mycompany.myapp.service.dto.MstBrandDTO;
import com.mycompany.myapp.service.dto.MstCategoryDTO;
import com.mycompany.myapp.service.dto.MstProductDTO;
import com.mycompany.myapp.service.dto.MstSupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstProduct} and its DTO {@link MstProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstProductMapper extends EntityMapper<MstProductDTO, MstProduct> {
    @Mapping(target = "category", source = "category", qualifiedByName = "mstCategoryId")
    @Mapping(target = "brand", source = "brand", qualifiedByName = "mstBrandId")
    @Mapping(target = "mstSupplier", source = "mstSupplier", qualifiedByName = "mstSupplierId")
    MstProductDTO toDto(MstProduct s);

    @Named("mstCategoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstCategoryDTO toDtoMstCategoryId(MstCategory mstCategory);

    @Named("mstBrandId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstBrandDTO toDtoMstBrandId(MstBrand mstBrand);

    @Named("mstSupplierId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstSupplierDTO toDtoMstSupplierId(MstSupplier mstSupplier);
}
