/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstTaskUpdate from './mst-task-update.vue';
import MstTaskService from './mst-task.service';
import AlertService from '@/shared/alert/alert.service';

import MstJobService from '@/entities/mst-job/mst-job.service';

type MstTaskUpdateComponentType = InstanceType<typeof MstTaskUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstTaskSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstTaskUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstTask Management Update Component', () => {
    let comp: MstTaskUpdateComponentType;
    let mstTaskServiceStub: SinonStubbedInstance<MstTaskService>;

    beforeEach(() => {
      route = {};
      mstTaskServiceStub = sinon.createStubInstance<MstTaskService>(MstTaskService);
      mstTaskServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstTaskService: () => mstTaskServiceStub,
          mstJobService: () =>
            sinon.createStubInstance<MstJobService>(MstJobService, {
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
        const wrapper = shallowMount(MstTaskUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstTask = mstTaskSample;
        mstTaskServiceStub.update.resolves(mstTaskSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstTaskServiceStub.update.calledWith(mstTaskSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstTaskServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstTaskUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstTask = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstTaskServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstTaskServiceStub.find.resolves(mstTaskSample);
        mstTaskServiceStub.retrieve.resolves([mstTaskSample]);

        // WHEN
        route = {
          params: {
            mstTaskId: '' + mstTaskSample.id,
          },
        };
        const wrapper = shallowMount(MstTaskUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstTask).toMatchObject(mstTaskSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstTaskServiceStub.find.resolves(mstTaskSample);
        const wrapper = shallowMount(MstTaskUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
