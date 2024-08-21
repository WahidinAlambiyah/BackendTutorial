/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import SubDistrictDetails from './sub-district-details.vue';
import SubDistrictService from './sub-district.service';
import AlertService from '@/shared/alert/alert.service';

type SubDistrictDetailsComponentType = InstanceType<typeof SubDistrictDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const subDistrictSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('SubDistrict Management Detail Component', () => {
    let subDistrictServiceStub: SinonStubbedInstance<SubDistrictService>;
    let mountOptions: MountingOptions<SubDistrictDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      subDistrictServiceStub = sinon.createStubInstance<SubDistrictService>(SubDistrictService);

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
          subDistrictService: () => subDistrictServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        subDistrictServiceStub.find.resolves(subDistrictSample);
        route = {
          params: {
            subDistrictId: '' + 123,
          },
        };
        const wrapper = shallowMount(SubDistrictDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.subDistrict).toMatchObject(subDistrictSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        subDistrictServiceStub.find.resolves(subDistrictSample);
        const wrapper = shallowMount(SubDistrictDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
