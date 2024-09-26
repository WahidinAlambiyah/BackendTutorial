/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstTaskDetails from './mst-task-details.vue';
import MstTaskService from './mst-task.service';
import AlertService from '@/shared/alert/alert.service';

type MstTaskDetailsComponentType = InstanceType<typeof MstTaskDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstTaskSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstTask Management Detail Component', () => {
    let mstTaskServiceStub: SinonStubbedInstance<MstTaskService>;
    let mountOptions: MountingOptions<MstTaskDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstTaskServiceStub = sinon.createStubInstance<MstTaskService>(MstTaskService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          mstTaskService: () => mstTaskServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstTaskServiceStub.find.resolves(mstTaskSample);
        route = {
          params: {
            mstTaskId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstTaskDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstTask).toMatchObject(mstTaskSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstTaskServiceStub.find.resolves(mstTaskSample);
        const wrapper = shallowMount(MstTaskDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
