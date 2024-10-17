/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstCustomerUpdate from './mst-customer-update.vue';
import MstCustomerService from './mst-customer.service';
import AlertService from '@/shared/alert/alert.service';

type MstCustomerUpdateComponentType = InstanceType<typeof MstCustomerUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstCustomerSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstCustomerUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstCustomer Management Update Component', () => {
    let comp: MstCustomerUpdateComponentType;
    let mstCustomerServiceStub: SinonStubbedInstance<MstCustomerService>;

    beforeEach(() => {
      route = {};
      mstCustomerServiceStub = sinon.createStubInstance<MstCustomerService>(MstCustomerService);
      mstCustomerServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          mstCustomerService: () => mstCustomerServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(MstCustomerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstCustomer = mstCustomerSample;
        mstCustomerServiceStub.update.resolves(mstCustomerSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstCustomerServiceStub.update.calledWith(mstCustomerSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstCustomerServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstCustomerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstCustomer = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstCustomerServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstCustomerServiceStub.find.resolves(mstCustomerSample);
        mstCustomerServiceStub.retrieve.resolves([mstCustomerSample]);

        // WHEN
        route = {
          params: {
            mstCustomerId: '' + mstCustomerSample.id,
          },
        };
        const wrapper = shallowMount(MstCustomerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstCustomer).toMatchObject(mstCustomerSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstCustomerServiceStub.find.resolves(mstCustomerSample);
        const wrapper = shallowMount(MstCustomerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
