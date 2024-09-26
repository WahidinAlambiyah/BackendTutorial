/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstSubDistrictDetails from './mst-sub-district-details.vue';
import MstSubDistrictService from './mst-sub-district.service';
import AlertService from '@/shared/alert/alert.service';

type MstSubDistrictDetailsComponentType = InstanceType<typeof MstSubDistrictDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstSubDistrictSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstSubDistrict Management Detail Component', () => {
    let mstSubDistrictServiceStub: SinonStubbedInstance<MstSubDistrictService>;
    let mountOptions: MountingOptions<MstSubDistrictDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstSubDistrictServiceStub = sinon.createStubInstance<MstSubDistrictService>(MstSubDistrictService);

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
          mstSubDistrictService: () => mstSubDistrictServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstSubDistrictServiceStub.find.resolves(mstSubDistrictSample);
        route = {
          params: {
            mstSubDistrictId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstSubDistrictDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstSubDistrict).toMatchObject(mstSubDistrictSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstSubDistrictServiceStub.find.resolves(mstSubDistrictSample);
        const wrapper = shallowMount(MstSubDistrictDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
