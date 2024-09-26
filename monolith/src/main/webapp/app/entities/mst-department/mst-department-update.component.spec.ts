/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstDepartmentUpdate from './mst-department-update.vue';
import MstDepartmentService from './mst-department.service';
import AlertService from '@/shared/alert/alert.service';

import LocationService from '@/entities/location/location.service';

type MstDepartmentUpdateComponentType = InstanceType<typeof MstDepartmentUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstDepartmentSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstDepartmentUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstDepartment Management Update Component', () => {
    let comp: MstDepartmentUpdateComponentType;
    let mstDepartmentServiceStub: SinonStubbedInstance<MstDepartmentService>;

    beforeEach(() => {
      route = {};
      mstDepartmentServiceStub = sinon.createStubInstance<MstDepartmentService>(MstDepartmentService);
      mstDepartmentServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstDepartmentService: () => mstDepartmentServiceStub,
          locationService: () =>
            sinon.createStubInstance<LocationService>(LocationService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(MstDepartmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstDepartment = mstDepartmentSample;
        mstDepartmentServiceStub.update.resolves(mstDepartmentSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstDepartmentServiceStub.update.calledWith(mstDepartmentSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstDepartmentServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstDepartmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstDepartment = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstDepartmentServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstDepartmentServiceStub.find.resolves(mstDepartmentSample);
        mstDepartmentServiceStub.retrieve.resolves([mstDepartmentSample]);

        // WHEN
        route = {
          params: {
            mstDepartmentId: '' + mstDepartmentSample.id,
          },
        };
        const wrapper = shallowMount(MstDepartmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstDepartment).toMatchObject(mstDepartmentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstDepartmentServiceStub.find.resolves(mstDepartmentSample);
        const wrapper = shallowMount(MstDepartmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
