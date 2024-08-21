import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const Region = () => import('@/entities/region/region.vue');
const RegionUpdate = () => import('@/entities/region/region-update.vue');
const RegionDetails = () => import('@/entities/region/region-details.vue');

const Country = () => import('@/entities/country/country.vue');
const CountryUpdate = () => import('@/entities/country/country-update.vue');
const CountryDetails = () => import('@/entities/country/country-details.vue');

const Location = () => import('@/entities/location/location.vue');
const LocationUpdate = () => import('@/entities/location/location-update.vue');
const LocationDetails = () => import('@/entities/location/location-details.vue');

const Department = () => import('@/entities/department/department.vue');
const DepartmentUpdate = () => import('@/entities/department/department-update.vue');
const DepartmentDetails = () => import('@/entities/department/department-details.vue');

const Task = () => import('@/entities/task/task.vue');
const TaskUpdate = () => import('@/entities/task/task-update.vue');
const TaskDetails = () => import('@/entities/task/task-details.vue');

const Employee = () => import('@/entities/employee/employee.vue');
const EmployeeUpdate = () => import('@/entities/employee/employee-update.vue');
const EmployeeDetails = () => import('@/entities/employee/employee-details.vue');

const Job = () => import('@/entities/job/job.vue');
const JobUpdate = () => import('@/entities/job/job-update.vue');
const JobDetails = () => import('@/entities/job/job-details.vue');

const JobHistory = () => import('@/entities/job-history/job-history.vue');
const JobHistoryUpdate = () => import('@/entities/job-history/job-history-update.vue');
const JobHistoryDetails = () => import('@/entities/job-history/job-history-details.vue');

const Province = () => import('@/entities/province/province.vue');
const ProvinceUpdate = () => import('@/entities/province/province-update.vue');
const ProvinceDetails = () => import('@/entities/province/province-details.vue');

const City = () => import('@/entities/city/city.vue');
const CityUpdate = () => import('@/entities/city/city-update.vue');
const CityDetails = () => import('@/entities/city/city-details.vue');

const District = () => import('@/entities/district/district.vue');
const DistrictUpdate = () => import('@/entities/district/district-update.vue');
const DistrictDetails = () => import('@/entities/district/district-details.vue');

const SubDistrict = () => import('@/entities/sub-district/sub-district.vue');
const SubDistrictUpdate = () => import('@/entities/sub-district/sub-district-update.vue');
const SubDistrictDetails = () => import('@/entities/sub-district/sub-district-details.vue');

const PostalCode = () => import('@/entities/postal-code/postal-code.vue');
const PostalCodeUpdate = () => import('@/entities/postal-code/postal-code-update.vue');
const PostalCodeDetails = () => import('@/entities/postal-code/postal-code-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'region',
      name: 'Region',
      component: Region,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'region/new',
      name: 'RegionCreate',
      component: RegionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'region/:regionId/edit',
      name: 'RegionEdit',
      component: RegionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'region/:regionId/view',
      name: 'RegionView',
      component: RegionDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'country',
      name: 'Country',
      component: Country,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'country/new',
      name: 'CountryCreate',
      component: CountryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'country/:countryId/edit',
      name: 'CountryEdit',
      component: CountryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'country/:countryId/view',
      name: 'CountryView',
      component: CountryDetails,
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
      path: 'department',
      name: 'Department',
      component: Department,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'department/new',
      name: 'DepartmentCreate',
      component: DepartmentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'department/:departmentId/edit',
      name: 'DepartmentEdit',
      component: DepartmentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'department/:departmentId/view',
      name: 'DepartmentView',
      component: DepartmentDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'task',
      name: 'Task',
      component: Task,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'task/new',
      name: 'TaskCreate',
      component: TaskUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'task/:taskId/edit',
      name: 'TaskEdit',
      component: TaskUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'task/:taskId/view',
      name: 'TaskView',
      component: TaskDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'employee',
      name: 'Employee',
      component: Employee,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'employee/new',
      name: 'EmployeeCreate',
      component: EmployeeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'employee/:employeeId/edit',
      name: 'EmployeeEdit',
      component: EmployeeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'employee/:employeeId/view',
      name: 'EmployeeView',
      component: EmployeeDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'job',
      name: 'Job',
      component: Job,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'job/new',
      name: 'JobCreate',
      component: JobUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'job/:jobId/edit',
      name: 'JobEdit',
      component: JobUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'job/:jobId/view',
      name: 'JobView',
      component: JobDetails,
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
      path: 'province',
      name: 'Province',
      component: Province,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'province/new',
      name: 'ProvinceCreate',
      component: ProvinceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'province/:provinceId/edit',
      name: 'ProvinceEdit',
      component: ProvinceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'province/:provinceId/view',
      name: 'ProvinceView',
      component: ProvinceDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'city',
      name: 'City',
      component: City,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'city/new',
      name: 'CityCreate',
      component: CityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'city/:cityId/edit',
      name: 'CityEdit',
      component: CityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'city/:cityId/view',
      name: 'CityView',
      component: CityDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'district',
      name: 'District',
      component: District,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'district/new',
      name: 'DistrictCreate',
      component: DistrictUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'district/:districtId/edit',
      name: 'DistrictEdit',
      component: DistrictUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'district/:districtId/view',
      name: 'DistrictView',
      component: DistrictDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'sub-district',
      name: 'SubDistrict',
      component: SubDistrict,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'sub-district/new',
      name: 'SubDistrictCreate',
      component: SubDistrictUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'sub-district/:subDistrictId/edit',
      name: 'SubDistrictEdit',
      component: SubDistrictUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'sub-district/:subDistrictId/view',
      name: 'SubDistrictView',
      component: SubDistrictDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'postal-code',
      name: 'PostalCode',
      component: PostalCode,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'postal-code/new',
      name: 'PostalCodeCreate',
      component: PostalCodeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'postal-code/:postalCodeId/edit',
      name: 'PostalCodeEdit',
      component: PostalCodeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'postal-code/:postalCodeId/view',
      name: 'PostalCodeView',
      component: PostalCodeDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
