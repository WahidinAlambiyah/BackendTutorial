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
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
