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
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
