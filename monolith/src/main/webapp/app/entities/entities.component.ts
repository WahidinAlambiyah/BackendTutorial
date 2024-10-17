import { defineComponent, provide } from 'vue';

import TrxEventService from './trx-event/trx-event.service';
import TrxTournamentService from './trx-tournament/trx-tournament.service';
import MstServiceService from './mst-service/mst-service.service';
import TrxTestimonialService from './trx-testimonial/trx-testimonial.service';
import MstRegionService from './mst-region/mst-region.service';
import MstCountryService from './mst-country/mst-country.service';
import MstProvinceService from './mst-province/mst-province.service';
import MstCityService from './mst-city/mst-city.service';
import MstDistrictService from './mst-district/mst-district.service';
import MstSubDistrictService from './mst-sub-district/mst-sub-district.service';
import MstPostalCodeService from './mst-postal-code/mst-postal-code.service';
import LocationService from './location/location.service';
import MstDepartmentService from './mst-department/mst-department.service';
import MstTaskService from './mst-task/mst-task.service';
import MstEmployeeService from './mst-employee/mst-employee.service';
import MstJobService from './mst-job/mst-job.service';
import JobHistoryService from './job-history/job-history.service';
import MstProductService from './mst-product/mst-product.service';
import MstCategoryService from './mst-category/mst-category.service';
import MstBrandService from './mst-brand/mst-brand.service';
import MstSupplierService from './mst-supplier/mst-supplier.service';
import StockService from './stock/stock.service';
import TrxOrderStockService from './trx-order-stock/trx-order-stock.service';
import TrxCartService from './trx-cart/trx-cart.service';
import TrxOrderService from './trx-order/trx-order.service';
import TrxOrderItemService from './trx-order-item/trx-order-item.service';
import MstCustomerService from './mst-customer/mst-customer.service';
import MstLoyaltyProgramService from './mst-loyalty-program/mst-loyalty-program.service';
import TrxDeliveryService from './trx-delivery/trx-delivery.service';
import MstDriverService from './mst-driver/mst-driver.service';
import TrxOrderHistoryService from './trx-order-history/trx-order-history.service';
import TrxProductHistoryService from './trx-product-history/trx-product-history.service';
import TrxDiscountService from './trx-discount/trx-discount.service';
import TrxCouponService from './trx-coupon/trx-coupon.service';
import TrxNotificationService from './trx-notification/trx-notification.service';
import TrxStockAlertService from './trx-stock-alert/trx-stock-alert.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('trxEventService', () => new TrxEventService());
    provide('trxTournamentService', () => new TrxTournamentService());
    provide('mstServiceService', () => new MstServiceService());
    provide('trxTestimonialService', () => new TrxTestimonialService());
    provide('mstRegionService', () => new MstRegionService());
    provide('mstCountryService', () => new MstCountryService());
    provide('mstProvinceService', () => new MstProvinceService());
    provide('mstCityService', () => new MstCityService());
    provide('mstDistrictService', () => new MstDistrictService());
    provide('mstSubDistrictService', () => new MstSubDistrictService());
    provide('mstPostalCodeService', () => new MstPostalCodeService());
    provide('locationService', () => new LocationService());
    provide('mstDepartmentService', () => new MstDepartmentService());
    provide('mstTaskService', () => new MstTaskService());
    provide('mstEmployeeService', () => new MstEmployeeService());
    provide('mstJobService', () => new MstJobService());
    provide('jobHistoryService', () => new JobHistoryService());
    provide('mstProductService', () => new MstProductService());
    provide('mstCategoryService', () => new MstCategoryService());
    provide('mstBrandService', () => new MstBrandService());
    provide('mstSupplierService', () => new MstSupplierService());
    provide('stockService', () => new StockService());
    provide('trxOrderStockService', () => new TrxOrderStockService());
    provide('trxCartService', () => new TrxCartService());
    provide('trxOrderService', () => new TrxOrderService());
    provide('trxOrderItemService', () => new TrxOrderItemService());
    provide('mstCustomerService', () => new MstCustomerService());
    provide('mstLoyaltyProgramService', () => new MstLoyaltyProgramService());
    provide('trxDeliveryService', () => new TrxDeliveryService());
    provide('mstDriverService', () => new MstDriverService());
    provide('trxOrderHistoryService', () => new TrxOrderHistoryService());
    provide('trxProductHistoryService', () => new TrxProductHistoryService());
    provide('trxDiscountService', () => new TrxDiscountService());
    provide('trxCouponService', () => new TrxCouponService());
    provide('trxNotificationService', () => new TrxNotificationService());
    provide('trxStockAlertService', () => new TrxStockAlertService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
