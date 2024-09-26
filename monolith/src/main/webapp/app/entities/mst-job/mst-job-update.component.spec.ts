/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstJobUpdate from './mst-job-update.vue';
import MstJobService from './mst-job.service';
import AlertService from '@/shared/alert/alert.service';

import MstTaskService from '@/entities/mst-task/mst-task.service';
import MstEmployeeService from '@/entities/mst-employee/mst-employee.service';

type MstJobUpdateComponentType = InstanceType<typeof MstJobUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstJobSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstJobUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstJob Management Update Component', () => {
    let comp: MstJobUpdateComponentType;
    let mstJobServiceStub: SinonStubbedInstance<MstJobService>;

    beforeEach(() => {
      route = {};
      mstJobServiceStub = sinon.createStubInstance<MstJobService>(MstJobService);
      mstJobServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstJobService: () => mstJobServiceStub,
          mstTaskService: () =>
            sinon.createStubInstance<MstTaskService>(MstTaskService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          mstEmployeeService: () =>
            sinon.createStubInstance<MstEmployeeService>(MstEmployeeService, {
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
        const wrapper = shallowMount(MstJobUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstJob = mstJobSample;
        mstJobServiceStub.update.resolves(mstJobSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstJobServiceStub.update.calledWith(mstJobSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstJobServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstJobUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstJob = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstJobServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstJobServiceStub.find.resolves(mstJobSample);
        mstJobServiceStub.retrieve.resolves([mstJobSample]);

        // WHEN
        route = {
          params: {
            mstJobId: '' + mstJobSample.id,
          },
        };
        const wrapper = shallowMount(MstJobUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstJob).toMatchObject(mstJobSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstJobServiceStub.find.resolves(mstJobSample);
        const wrapper = shallowMount(MstJobUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
