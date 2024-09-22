/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import PostalCodeDetails from './postal-code-details.vue';
import PostalCodeService from './postal-code.service';
import AlertService from '@/shared/alert/alert.service';

type PostalCodeDetailsComponentType = InstanceType<typeof PostalCodeDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const postalCodeSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('PostalCode Management Detail Component', () => {
    let postalCodeServiceStub: SinonStubbedInstance<PostalCodeService>;
    let mountOptions: MountingOptions<PostalCodeDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      postalCodeServiceStub = sinon.createStubInstance<PostalCodeService>(PostalCodeService);

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
          postalCodeService: () => postalCodeServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        postalCodeServiceStub.find.resolves(postalCodeSample);
        route = {
          params: {
            postalCodeId: '' + 123,
          },
        };
        const wrapper = shallowMount(PostalCodeDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.postalCode).toMatchObject(postalCodeSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        postalCodeServiceStub.find.resolves(postalCodeSample);
        const wrapper = shallowMount(PostalCodeDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
