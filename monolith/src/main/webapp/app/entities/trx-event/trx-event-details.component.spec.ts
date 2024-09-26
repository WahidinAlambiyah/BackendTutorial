/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxEventDetails from './trx-event-details.vue';
import TrxEventService from './trx-event.service';
import AlertService from '@/shared/alert/alert.service';

type TrxEventDetailsComponentType = InstanceType<typeof TrxEventDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxEventSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrxEvent Management Detail Component', () => {
    let trxEventServiceStub: SinonStubbedInstance<TrxEventService>;
    let mountOptions: MountingOptions<TrxEventDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trxEventServiceStub = sinon.createStubInstance<TrxEventService>(TrxEventService);

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
          trxEventService: () => trxEventServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxEventServiceStub.find.resolves(trxEventSample);
        route = {
          params: {
            trxEventId: '' + 123,
          },
        };
        const wrapper = shallowMount(TrxEventDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trxEvent).toMatchObject(trxEventSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxEventServiceStub.find.resolves(trxEventSample);
        const wrapper = shallowMount(TrxEventDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
