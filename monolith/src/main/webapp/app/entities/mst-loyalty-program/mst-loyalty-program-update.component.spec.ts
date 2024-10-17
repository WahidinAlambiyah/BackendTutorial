/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstLoyaltyProgramUpdate from './mst-loyalty-program-update.vue';
import MstLoyaltyProgramService from './mst-loyalty-program.service';
import AlertService from '@/shared/alert/alert.service';

import MstCustomerService from '@/entities/mst-customer/mst-customer.service';

type MstLoyaltyProgramUpdateComponentType = InstanceType<typeof MstLoyaltyProgramUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstLoyaltyProgramSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstLoyaltyProgramUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstLoyaltyProgram Management Update Component', () => {
    let comp: MstLoyaltyProgramUpdateComponentType;
    let mstLoyaltyProgramServiceStub: SinonStubbedInstance<MstLoyaltyProgramService>;

    beforeEach(() => {
      route = {};
      mstLoyaltyProgramServiceStub = sinon.createStubInstance<MstLoyaltyProgramService>(MstLoyaltyProgramService);
      mstLoyaltyProgramServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstLoyaltyProgramService: () => mstLoyaltyProgramServiceStub,
          mstCustomerService: () =>
            sinon.createStubInstance<MstCustomerService>(MstCustomerService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(MstLoyaltyProgramUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstLoyaltyProgram = mstLoyaltyProgramSample;
        mstLoyaltyProgramServiceStub.update.resolves(mstLoyaltyProgramSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstLoyaltyProgramServiceStub.update.calledWith(mstLoyaltyProgramSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstLoyaltyProgramServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstLoyaltyProgramUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstLoyaltyProgram = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstLoyaltyProgramServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstLoyaltyProgramServiceStub.find.resolves(mstLoyaltyProgramSample);
        mstLoyaltyProgramServiceStub.retrieve.resolves([mstLoyaltyProgramSample]);

        // WHEN
        route = {
          params: {
            mstLoyaltyProgramId: '' + mstLoyaltyProgramSample.id,
          },
        };
        const wrapper = shallowMount(MstLoyaltyProgramUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstLoyaltyProgram).toMatchObject(mstLoyaltyProgramSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstLoyaltyProgramServiceStub.find.resolves(mstLoyaltyProgramSample);
        const wrapper = shallowMount(MstLoyaltyProgramUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
