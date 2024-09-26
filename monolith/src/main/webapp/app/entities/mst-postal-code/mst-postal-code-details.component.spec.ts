/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstPostalCodeDetails from './mst-postal-code-details.vue';
import MstPostalCodeService from './mst-postal-code.service';
import AlertService from '@/shared/alert/alert.service';

type MstPostalCodeDetailsComponentType = InstanceType<typeof MstPostalCodeDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstPostalCodeSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstPostalCode Management Detail Component', () => {
    let mstPostalCodeServiceStub: SinonStubbedInstance<MstPostalCodeService>;
    let mountOptions: MountingOptions<MstPostalCodeDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstPostalCodeServiceStub = sinon.createStubInstance<MstPostalCodeService>(MstPostalCodeService);

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
          mstPostalCodeService: () => mstPostalCodeServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstPostalCodeServiceStub.find.resolves(mstPostalCodeSample);
        route = {
          params: {
            mstPostalCodeId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstPostalCodeDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstPostalCode).toMatchObject(mstPostalCodeSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstPostalCodeServiceStub.find.resolves(mstPostalCodeSample);
        const wrapper = shallowMount(MstPostalCodeDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
