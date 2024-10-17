import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const TrxEvent = () => import('@/entities/trx-event/trx-event.vue');
const TrxEventUpdate = () => import('@/entities/trx-event/trx-event-update.vue');
const TrxEventDetails = () => import('@/entities/trx-event/trx-event-details.vue');

const TrxTournament = () => import('@/entities/trx-tournament/trx-tournament.vue');
const TrxTournamentUpdate = () => import('@/entities/trx-tournament/trx-tournament-update.vue');
const TrxTournamentDetails = () => import('@/entities/trx-tournament/trx-tournament-details.vue');

const MstService = () => import('@/entities/mst-service/mst-service.vue');
const MstServiceUpdate = () => import('@/entities/mst-service/mst-service-update.vue');
const MstServiceDetails = () => import('@/entities/mst-service/mst-service-details.vue');

const TrxTestimonial = () => import('@/entities/trx-testimonial/trx-testimonial.vue');
const TrxTestimonialUpdate = () => import('@/entities/trx-testimonial/trx-testimonial-update.vue');
const TrxTestimonialDetails = () => import('@/entities/trx-testimonial/trx-testimonial-details.vue');

const MstRegion = () => import('@/entities/mst-region/mst-region.vue');
const MstRegionUpdate = () => import('@/entities/mst-region/mst-region-update.vue');
const MstRegionDetails = () => import('@/entities/mst-region/mst-region-details.vue');

const MstCountry = () => import('@/entities/mst-country/mst-country.vue');
const MstCountryUpdate = () => import('@/entities/mst-country/mst-country-update.vue');
const MstCountryDetails = () => import('@/entities/mst-country/mst-country-details.vue');

const MstProvince = () => import('@/entities/mst-province/mst-province.vue');
const MstProvinceUpdate = () => import('@/entities/mst-province/mst-province-update.vue');
const MstProvinceDetails = () => import('@/entities/mst-province/mst-province-details.vue');

const MstCity = () => import('@/entities/mst-city/mst-city.vue');
const MstCityUpdate = () => import('@/entities/mst-city/mst-city-update.vue');
const MstCityDetails = () => import('@/entities/mst-city/mst-city-details.vue');

const MstDistrict = () => import('@/entities/mst-district/mst-district.vue');
const MstDistrictUpdate = () => import('@/entities/mst-district/mst-district-update.vue');
const MstDistrictDetails = () => import('@/entities/mst-district/mst-district-details.vue');

const MstSubDistrict = () => import('@/entities/mst-sub-district/mst-sub-district.vue');
const MstSubDistrictUpdate = () => import('@/entities/mst-sub-district/mst-sub-district-update.vue');
const MstSubDistrictDetails = () => import('@/entities/mst-sub-district/mst-sub-district-details.vue');

const MstPostalCode = () => import('@/entities/mst-postal-code/mst-postal-code.vue');
const MstPostalCodeUpdate = () => import('@/entities/mst-postal-code/mst-postal-code-update.vue');
const MstPostalCodeDetails = () => import('@/entities/mst-postal-code/mst-postal-code-details.vue');

const Location = () => import('@/entities/location/location.vue');
const LocationUpdate = () => import('@/entities/location/location-update.vue');
const LocationDetails = () => import('@/entities/location/location-details.vue');

const MstDepartment = () => import('@/entities/mst-department/mst-department.vue');
const MstDepartmentUpdate = () => import('@/entities/mst-department/mst-department-update.vue');
const MstDepartmentDetails = () => import('@/entities/mst-department/mst-department-details.vue');

const MstTask = () => import('@/entities/mst-task/mst-task.vue');
const MstTaskUpdate = () => import('@/entities/mst-task/mst-task-update.vue');
const MstTaskDetails = () => import('@/entities/mst-task/mst-task-details.vue');

const MstEmployee = () => import('@/entities/mst-employee/mst-employee.vue');
const MstEmployeeUpdate = () => import('@/entities/mst-employee/mst-employee-update.vue');
const MstEmployeeDetails = () => import('@/entities/mst-employee/mst-employee-details.vue');

const MstJob = () => import('@/entities/mst-job/mst-job.vue');
const MstJobUpdate = () => import('@/entities/mst-job/mst-job-update.vue');
const MstJobDetails = () => import('@/entities/mst-job/mst-job-details.vue');

const JobHistory = () => import('@/entities/job-history/job-history.vue');
const JobHistoryUpdate = () => import('@/entities/job-history/job-history-update.vue');
const JobHistoryDetails = () => import('@/entities/job-history/job-history-details.vue');

const MstProduct = () => import('@/entities/mst-product/mst-product.vue');
const MstProductUpdate = () => import('@/entities/mst-product/mst-product-update.vue');
const MstProductDetails = () => import('@/entities/mst-product/mst-product-details.vue');

const MstCategory = () => import('@/entities/mst-category/mst-category.vue');
const MstCategoryUpdate = () => import('@/entities/mst-category/mst-category-update.vue');
const MstCategoryDetails = () => import('@/entities/mst-category/mst-category-details.vue');

const MstBrand = () => import('@/entities/mst-brand/mst-brand.vue');
const MstBrandUpdate = () => import('@/entities/mst-brand/mst-brand-update.vue');
const MstBrandDetails = () => import('@/entities/mst-brand/mst-brand-details.vue');

const MstSupplier = () => import('@/entities/mst-supplier/mst-supplier.vue');
const MstSupplierUpdate = () => import('@/entities/mst-supplier/mst-supplier-update.vue');
const MstSupplierDetails = () => import('@/entities/mst-supplier/mst-supplier-details.vue');

const Stock = () => import('@/entities/stock/stock.vue');
const StockUpdate = () => import('@/entities/stock/stock-update.vue');
const StockDetails = () => import('@/entities/stock/stock-details.vue');

const TrxOrderStock = () => import('@/entities/trx-order-stock/trx-order-stock.vue');
const TrxOrderStockUpdate = () => import('@/entities/trx-order-stock/trx-order-stock-update.vue');
const TrxOrderStockDetails = () => import('@/entities/trx-order-stock/trx-order-stock-details.vue');

const TrxCart = () => import('@/entities/trx-cart/trx-cart.vue');
const TrxCartUpdate = () => import('@/entities/trx-cart/trx-cart-update.vue');
const TrxCartDetails = () => import('@/entities/trx-cart/trx-cart-details.vue');

const TrxOrder = () => import('@/entities/trx-order/trx-order.vue');
const TrxOrderUpdate = () => import('@/entities/trx-order/trx-order-update.vue');
const TrxOrderDetails = () => import('@/entities/trx-order/trx-order-details.vue');

const TrxOrderItem = () => import('@/entities/trx-order-item/trx-order-item.vue');
const TrxOrderItemUpdate = () => import('@/entities/trx-order-item/trx-order-item-update.vue');
const TrxOrderItemDetails = () => import('@/entities/trx-order-item/trx-order-item-details.vue');

const MstCustomer = () => import('@/entities/mst-customer/mst-customer.vue');
const MstCustomerUpdate = () => import('@/entities/mst-customer/mst-customer-update.vue');
const MstCustomerDetails = () => import('@/entities/mst-customer/mst-customer-details.vue');

const MstLoyaltyProgram = () => import('@/entities/mst-loyalty-program/mst-loyalty-program.vue');
const MstLoyaltyProgramUpdate = () => import('@/entities/mst-loyalty-program/mst-loyalty-program-update.vue');
const MstLoyaltyProgramDetails = () => import('@/entities/mst-loyalty-program/mst-loyalty-program-details.vue');

const TrxDelivery = () => import('@/entities/trx-delivery/trx-delivery.vue');
const TrxDeliveryUpdate = () => import('@/entities/trx-delivery/trx-delivery-update.vue');
const TrxDeliveryDetails = () => import('@/entities/trx-delivery/trx-delivery-details.vue');

const MstDriver = () => import('@/entities/mst-driver/mst-driver.vue');
const MstDriverUpdate = () => import('@/entities/mst-driver/mst-driver-update.vue');
const MstDriverDetails = () => import('@/entities/mst-driver/mst-driver-details.vue');

const TrxOrderHistory = () => import('@/entities/trx-order-history/trx-order-history.vue');
const TrxOrderHistoryUpdate = () => import('@/entities/trx-order-history/trx-order-history-update.vue');
const TrxOrderHistoryDetails = () => import('@/entities/trx-order-history/trx-order-history-details.vue');

const TrxProductHistory = () => import('@/entities/trx-product-history/trx-product-history.vue');
const TrxProductHistoryUpdate = () => import('@/entities/trx-product-history/trx-product-history-update.vue');
const TrxProductHistoryDetails = () => import('@/entities/trx-product-history/trx-product-history-details.vue');

const TrxDiscount = () => import('@/entities/trx-discount/trx-discount.vue');
const TrxDiscountUpdate = () => import('@/entities/trx-discount/trx-discount-update.vue');
const TrxDiscountDetails = () => import('@/entities/trx-discount/trx-discount-details.vue');

const TrxCoupon = () => import('@/entities/trx-coupon/trx-coupon.vue');
const TrxCouponUpdate = () => import('@/entities/trx-coupon/trx-coupon-update.vue');
const TrxCouponDetails = () => import('@/entities/trx-coupon/trx-coupon-details.vue');

const TrxNotification = () => import('@/entities/trx-notification/trx-notification.vue');
const TrxNotificationUpdate = () => import('@/entities/trx-notification/trx-notification-update.vue');
const TrxNotificationDetails = () => import('@/entities/trx-notification/trx-notification-details.vue');

const TrxStockAlert = () => import('@/entities/trx-stock-alert/trx-stock-alert.vue');
const TrxStockAlertUpdate = () => import('@/entities/trx-stock-alert/trx-stock-alert-update.vue');
const TrxStockAlertDetails = () => import('@/entities/trx-stock-alert/trx-stock-alert-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'trx-event',
      name: 'TrxEvent',
      component: TrxEvent,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-event/new',
      name: 'TrxEventCreate',
      component: TrxEventUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-event/:trxEventId/edit',
      name: 'TrxEventEdit',
      component: TrxEventUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-event/:trxEventId/view',
      name: 'TrxEventView',
      component: TrxEventDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-tournament',
      name: 'TrxTournament',
      component: TrxTournament,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-tournament/new',
      name: 'TrxTournamentCreate',
      component: TrxTournamentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-tournament/:trxTournamentId/edit',
      name: 'TrxTournamentEdit',
      component: TrxTournamentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-tournament/:trxTournamentId/view',
      name: 'TrxTournamentView',
      component: TrxTournamentDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-service',
      name: 'MstService',
      component: MstService,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-service/new',
      name: 'MstServiceCreate',
      component: MstServiceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-service/:mstServiceId/edit',
      name: 'MstServiceEdit',
      component: MstServiceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-service/:mstServiceId/view',
      name: 'MstServiceView',
      component: MstServiceDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-testimonial',
      name: 'TrxTestimonial',
      component: TrxTestimonial,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-testimonial/new',
      name: 'TrxTestimonialCreate',
      component: TrxTestimonialUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-testimonial/:trxTestimonialId/edit',
      name: 'TrxTestimonialEdit',
      component: TrxTestimonialUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-testimonial/:trxTestimonialId/view',
      name: 'TrxTestimonialView',
      component: TrxTestimonialDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-region',
      name: 'MstRegion',
      component: MstRegion,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-region/new',
      name: 'MstRegionCreate',
      component: MstRegionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-region/:mstRegionId/edit',
      name: 'MstRegionEdit',
      component: MstRegionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-region/:mstRegionId/view',
      name: 'MstRegionView',
      component: MstRegionDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-country',
      name: 'MstCountry',
      component: MstCountry,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-country/new',
      name: 'MstCountryCreate',
      component: MstCountryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-country/:mstCountryId/edit',
      name: 'MstCountryEdit',
      component: MstCountryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-country/:mstCountryId/view',
      name: 'MstCountryView',
      component: MstCountryDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-province',
      name: 'MstProvince',
      component: MstProvince,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-province/new',
      name: 'MstProvinceCreate',
      component: MstProvinceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-province/:mstProvinceId/edit',
      name: 'MstProvinceEdit',
      component: MstProvinceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-province/:mstProvinceId/view',
      name: 'MstProvinceView',
      component: MstProvinceDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-city',
      name: 'MstCity',
      component: MstCity,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-city/new',
      name: 'MstCityCreate',
      component: MstCityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-city/:mstCityId/edit',
      name: 'MstCityEdit',
      component: MstCityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-city/:mstCityId/view',
      name: 'MstCityView',
      component: MstCityDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-district',
      name: 'MstDistrict',
      component: MstDistrict,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-district/new',
      name: 'MstDistrictCreate',
      component: MstDistrictUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-district/:mstDistrictId/edit',
      name: 'MstDistrictEdit',
      component: MstDistrictUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-district/:mstDistrictId/view',
      name: 'MstDistrictView',
      component: MstDistrictDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-sub-district',
      name: 'MstSubDistrict',
      component: MstSubDistrict,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-sub-district/new',
      name: 'MstSubDistrictCreate',
      component: MstSubDistrictUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-sub-district/:mstSubDistrictId/edit',
      name: 'MstSubDistrictEdit',
      component: MstSubDistrictUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-sub-district/:mstSubDistrictId/view',
      name: 'MstSubDistrictView',
      component: MstSubDistrictDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-postal-code',
      name: 'MstPostalCode',
      component: MstPostalCode,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-postal-code/new',
      name: 'MstPostalCodeCreate',
      component: MstPostalCodeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-postal-code/:mstPostalCodeId/edit',
      name: 'MstPostalCodeEdit',
      component: MstPostalCodeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-postal-code/:mstPostalCodeId/view',
      name: 'MstPostalCodeView',
      component: MstPostalCodeDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location',
      name: 'Location',
      component: Location,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location/new',
      name: 'LocationCreate',
      component: LocationUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location/:locationId/edit',
      name: 'LocationEdit',
      component: LocationUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location/:locationId/view',
      name: 'LocationView',
      component: LocationDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-department',
      name: 'MstDepartment',
      component: MstDepartment,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-department/new',
      name: 'MstDepartmentCreate',
      component: MstDepartmentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-department/:mstDepartmentId/edit',
      name: 'MstDepartmentEdit',
      component: MstDepartmentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-department/:mstDepartmentId/view',
      name: 'MstDepartmentView',
      component: MstDepartmentDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-task',
      name: 'MstTask',
      component: MstTask,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-task/new',
      name: 'MstTaskCreate',
      component: MstTaskUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-task/:mstTaskId/edit',
      name: 'MstTaskEdit',
      component: MstTaskUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-task/:mstTaskId/view',
      name: 'MstTaskView',
      component: MstTaskDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-employee',
      name: 'MstEmployee',
      component: MstEmployee,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-employee/new',
      name: 'MstEmployeeCreate',
      component: MstEmployeeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-employee/:mstEmployeeId/edit',
      name: 'MstEmployeeEdit',
      component: MstEmployeeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-employee/:mstEmployeeId/view',
      name: 'MstEmployeeView',
      component: MstEmployeeDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-job',
      name: 'MstJob',
      component: MstJob,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-job/new',
      name: 'MstJobCreate',
      component: MstJobUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-job/:mstJobId/edit',
      name: 'MstJobEdit',
      component: MstJobUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-job/:mstJobId/view',
      name: 'MstJobView',
      component: MstJobDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'job-history',
      name: 'JobHistory',
      component: JobHistory,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'job-history/new',
      name: 'JobHistoryCreate',
      component: JobHistoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'job-history/:jobHistoryId/edit',
      name: 'JobHistoryEdit',
      component: JobHistoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'job-history/:jobHistoryId/view',
      name: 'JobHistoryView',
      component: JobHistoryDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-product',
      name: 'MstProduct',
      component: MstProduct,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-product/new',
      name: 'MstProductCreate',
      component: MstProductUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-product/:mstProductId/edit',
      name: 'MstProductEdit',
      component: MstProductUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-product/:mstProductId/view',
      name: 'MstProductView',
      component: MstProductDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-category',
      name: 'MstCategory',
      component: MstCategory,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-category/new',
      name: 'MstCategoryCreate',
      component: MstCategoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-category/:mstCategoryId/edit',
      name: 'MstCategoryEdit',
      component: MstCategoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-category/:mstCategoryId/view',
      name: 'MstCategoryView',
      component: MstCategoryDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-brand',
      name: 'MstBrand',
      component: MstBrand,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-brand/new',
      name: 'MstBrandCreate',
      component: MstBrandUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-brand/:mstBrandId/edit',
      name: 'MstBrandEdit',
      component: MstBrandUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-brand/:mstBrandId/view',
      name: 'MstBrandView',
      component: MstBrandDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-supplier',
      name: 'MstSupplier',
      component: MstSupplier,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-supplier/new',
      name: 'MstSupplierCreate',
      component: MstSupplierUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-supplier/:mstSupplierId/edit',
      name: 'MstSupplierEdit',
      component: MstSupplierUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-supplier/:mstSupplierId/view',
      name: 'MstSupplierView',
      component: MstSupplierDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'stock',
      name: 'Stock',
      component: Stock,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'stock/new',
      name: 'StockCreate',
      component: StockUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'stock/:stockId/edit',
      name: 'StockEdit',
      component: StockUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'stock/:stockId/view',
      name: 'StockView',
      component: StockDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order-stock',
      name: 'TrxOrderStock',
      component: TrxOrderStock,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order-stock/new',
      name: 'TrxOrderStockCreate',
      component: TrxOrderStockUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order-stock/:trxOrderStockId/edit',
      name: 'TrxOrderStockEdit',
      component: TrxOrderStockUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order-stock/:trxOrderStockId/view',
      name: 'TrxOrderStockView',
      component: TrxOrderStockDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-cart',
      name: 'TrxCart',
      component: TrxCart,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-cart/new',
      name: 'TrxCartCreate',
      component: TrxCartUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-cart/:trxCartId/edit',
      name: 'TrxCartEdit',
      component: TrxCartUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-cart/:trxCartId/view',
      name: 'TrxCartView',
      component: TrxCartDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order',
      name: 'TrxOrder',
      component: TrxOrder,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order/new',
      name: 'TrxOrderCreate',
      component: TrxOrderUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order/:trxOrderId/edit',
      name: 'TrxOrderEdit',
      component: TrxOrderUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order/:trxOrderId/view',
      name: 'TrxOrderView',
      component: TrxOrderDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order-item',
      name: 'TrxOrderItem',
      component: TrxOrderItem,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order-item/new',
      name: 'TrxOrderItemCreate',
      component: TrxOrderItemUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order-item/:trxOrderItemId/edit',
      name: 'TrxOrderItemEdit',
      component: TrxOrderItemUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order-item/:trxOrderItemId/view',
      name: 'TrxOrderItemView',
      component: TrxOrderItemDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-customer',
      name: 'MstCustomer',
      component: MstCustomer,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-customer/new',
      name: 'MstCustomerCreate',
      component: MstCustomerUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-customer/:mstCustomerId/edit',
      name: 'MstCustomerEdit',
      component: MstCustomerUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-customer/:mstCustomerId/view',
      name: 'MstCustomerView',
      component: MstCustomerDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-loyalty-program',
      name: 'MstLoyaltyProgram',
      component: MstLoyaltyProgram,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-loyalty-program/new',
      name: 'MstLoyaltyProgramCreate',
      component: MstLoyaltyProgramUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-loyalty-program/:mstLoyaltyProgramId/edit',
      name: 'MstLoyaltyProgramEdit',
      component: MstLoyaltyProgramUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-loyalty-program/:mstLoyaltyProgramId/view',
      name: 'MstLoyaltyProgramView',
      component: MstLoyaltyProgramDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-delivery',
      name: 'TrxDelivery',
      component: TrxDelivery,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-delivery/new',
      name: 'TrxDeliveryCreate',
      component: TrxDeliveryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-delivery/:trxDeliveryId/edit',
      name: 'TrxDeliveryEdit',
      component: TrxDeliveryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-delivery/:trxDeliveryId/view',
      name: 'TrxDeliveryView',
      component: TrxDeliveryDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-driver',
      name: 'MstDriver',
      component: MstDriver,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-driver/new',
      name: 'MstDriverCreate',
      component: MstDriverUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-driver/:mstDriverId/edit',
      name: 'MstDriverEdit',
      component: MstDriverUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mst-driver/:mstDriverId/view',
      name: 'MstDriverView',
      component: MstDriverDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order-history',
      name: 'TrxOrderHistory',
      component: TrxOrderHistory,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order-history/new',
      name: 'TrxOrderHistoryCreate',
      component: TrxOrderHistoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order-history/:trxOrderHistoryId/edit',
      name: 'TrxOrderHistoryEdit',
      component: TrxOrderHistoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-order-history/:trxOrderHistoryId/view',
      name: 'TrxOrderHistoryView',
      component: TrxOrderHistoryDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-product-history',
      name: 'TrxProductHistory',
      component: TrxProductHistory,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-product-history/new',
      name: 'TrxProductHistoryCreate',
      component: TrxProductHistoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-product-history/:trxProductHistoryId/edit',
      name: 'TrxProductHistoryEdit',
      component: TrxProductHistoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-product-history/:trxProductHistoryId/view',
      name: 'TrxProductHistoryView',
      component: TrxProductHistoryDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-discount',
      name: 'TrxDiscount',
      component: TrxDiscount,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-discount/new',
      name: 'TrxDiscountCreate',
      component: TrxDiscountUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-discount/:trxDiscountId/edit',
      name: 'TrxDiscountEdit',
      component: TrxDiscountUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-discount/:trxDiscountId/view',
      name: 'TrxDiscountView',
      component: TrxDiscountDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-coupon',
      name: 'TrxCoupon',
      component: TrxCoupon,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-coupon/new',
      name: 'TrxCouponCreate',
      component: TrxCouponUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-coupon/:trxCouponId/edit',
      name: 'TrxCouponEdit',
      component: TrxCouponUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-coupon/:trxCouponId/view',
      name: 'TrxCouponView',
      component: TrxCouponDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-notification',
      name: 'TrxNotification',
      component: TrxNotification,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-notification/new',
      name: 'TrxNotificationCreate',
      component: TrxNotificationUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-notification/:trxNotificationId/edit',
      name: 'TrxNotificationEdit',
      component: TrxNotificationUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-notification/:trxNotificationId/view',
      name: 'TrxNotificationView',
      component: TrxNotificationDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-stock-alert',
      name: 'TrxStockAlert',
      component: TrxStockAlert,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-stock-alert/new',
      name: 'TrxStockAlertCreate',
      component: TrxStockAlertUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-stock-alert/:trxStockAlertId/edit',
      name: 'TrxStockAlertEdit',
      component: TrxStockAlertUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'trx-stock-alert/:trxStockAlertId/view',
      name: 'TrxStockAlertView',
      component: TrxStockAlertDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
