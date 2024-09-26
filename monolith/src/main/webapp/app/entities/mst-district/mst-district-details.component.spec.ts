/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstDistrictDetails from './mst-district-details.vue';
import MstDistrictService from './mst-district.service';
import AlertService from '@/shared/alert/alert.service';

type MstDistrictDetailsComponentType = InstanceType<typeof MstDistrictDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstDistrictSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstDistrict Management Detail Component', () => {
    let mstDistrictServiceStub: SinonStubbedInstance<MstDistrictService>;
    let mountOptions: MountingOptions<MstDistrictDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstDistrictServiceStub = sinon.createStubInstance<MstDistrictService>(MstDistrictService);

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
          mstDistrictService: () => mstDistrictServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstDistrictServiceStub.find.resolves(mstDistrictSample);
        route = {
          params: {
            mstDistrictId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstDistrictDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstDistrict).toMatchObject(mstDistrictSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstDistrictServiceStub.find.resolves(mstDistrictSample);
        const wrapper = shallowMount(MstDistrictDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
