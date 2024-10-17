/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxNotificationDetails from './trx-notification-details.vue';
import TrxNotificationService from './trx-notification.service';
import AlertService from '@/shared/alert/alert.service';

type TrxNotificationDetailsComponentType = InstanceType<typeof TrxNotificationDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxNotificationSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrxNotification Management Detail Component', () => {
    let trxNotificationServiceStub: SinonStubbedInstance<TrxNotificationService>;
    let mountOptions: MountingOptions<TrxNotificationDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trxNotificationServiceStub = sinon.createStubInstance<TrxNotificationService>(TrxNotificationService);

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
          trxNotificationService: () => trxNotificationServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxNotificationServiceStub.find.resolves(trxNotificationSample);
        route = {
          params: {
            trxNotificationId: '' + 123,
          },
        };
        const wrapper = shallowMount(TrxNotificationDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trxNotification).toMatchObject(trxNotificationSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxNotificationServiceStub.find.resolves(trxNotificationSample);
        const wrapper = shallowMount(TrxNotificationDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
