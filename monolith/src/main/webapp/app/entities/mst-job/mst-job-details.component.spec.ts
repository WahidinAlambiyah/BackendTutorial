/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstJobDetails from './mst-job-details.vue';
import MstJobService from './mst-job.service';
import AlertService from '@/shared/alert/alert.service';

type MstJobDetailsComponentType = InstanceType<typeof MstJobDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstJobSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstJob Management Detail Component', () => {
    let mstJobServiceStub: SinonStubbedInstance<MstJobService>;
    let mountOptions: MountingOptions<MstJobDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstJobServiceStub = sinon.createStubInstance<MstJobService>(MstJobService);

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
          mstJobService: () => mstJobServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstJobServiceStub.find.resolves(mstJobSample);
        route = {
          params: {
            mstJobId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstJobDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstJob).toMatchObject(mstJobSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstJobServiceStub.find.resolves(mstJobSample);
        const wrapper = shallowMount(MstJobDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
