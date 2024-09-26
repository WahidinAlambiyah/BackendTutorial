/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstCountryDetails from './mst-country-details.vue';
import MstCountryService from './mst-country.service';
import AlertService from '@/shared/alert/alert.service';

type MstCountryDetailsComponentType = InstanceType<typeof MstCountryDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstCountrySample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstCountry Management Detail Component', () => {
    let mstCountryServiceStub: SinonStubbedInstance<MstCountryService>;
    let mountOptions: MountingOptions<MstCountryDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstCountryServiceStub = sinon.createStubInstance<MstCountryService>(MstCountryService);

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
          mstCountryService: () => mstCountryServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstCountryServiceStub.find.resolves(mstCountrySample);
        route = {
          params: {
            mstCountryId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstCountryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstCountry).toMatchObject(mstCountrySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstCountryServiceStub.find.resolves(mstCountrySample);
        const wrapper = shallowMount(MstCountryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
