/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import MstEmployeeUpdate from './mst-employee-update.vue';
import MstEmployeeService from './mst-employee.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import MstDepartmentService from '@/entities/mst-department/mst-department.service';

type MstEmployeeUpdateComponentType = InstanceType<typeof MstEmployeeUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstEmployeeSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstEmployeeUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstEmployee Management Update Component', () => {
    let comp: MstEmployeeUpdateComponentType;
    let mstEmployeeServiceStub: SinonStubbedInstance<MstEmployeeService>;

    beforeEach(() => {
      route = {};
      mstEmployeeServiceStub = sinon.createStubInstance<MstEmployeeService>(MstEmployeeService);
      mstEmployeeServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          mstEmployeeService: () => mstEmployeeServiceStub,
          mstDepartmentService: () =>
            sinon.createStubInstance<MstDepartmentService>(MstDepartmentService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(MstEmployeeUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(MstEmployeeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstEmployee = mstEmployeeSample;
        mstEmployeeServiceStub.update.resolves(mstEmployeeSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstEmployeeServiceStub.update.calledWith(mstEmployeeSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstEmployeeServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstEmployeeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstEmployee = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstEmployeeServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstEmployeeServiceStub.find.resolves(mstEmployeeSample);
        mstEmployeeServiceStub.retrieve.resolves([mstEmployeeSample]);

        // WHEN
        route = {
          params: {
            mstEmployeeId: '' + mstEmployeeSample.id,
          },
        };
        const wrapper = shallowMount(MstEmployeeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstEmployee).toMatchObject(mstEmployeeSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstEmployeeServiceStub.find.resolves(mstEmployeeSample);
        const wrapper = shallowMount(MstEmployeeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
