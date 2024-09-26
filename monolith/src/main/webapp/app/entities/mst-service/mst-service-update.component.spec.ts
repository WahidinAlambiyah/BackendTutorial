/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstServiceUpdate from './mst-service-update.vue';
import MstServiceService from './mst-service.service';
import AlertService from '@/shared/alert/alert.service';

import TrxTestimonialService from '@/entities/trx-testimonial/trx-testimonial.service';

type MstServiceUpdateComponentType = InstanceType<typeof MstServiceUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstServiceSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstServiceUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstService Management Update Component', () => {
    let comp: MstServiceUpdateComponentType;
    let mstServiceServiceStub: SinonStubbedInstance<MstServiceService>;

    beforeEach(() => {
      route = {};
      mstServiceServiceStub = sinon.createStubInstance<MstServiceService>(MstServiceService);
      mstServiceServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstServiceService: () => mstServiceServiceStub,
          trxTestimonialService: () =>
            sinon.createStubInstance<TrxTestimonialService>(TrxTestimonialService, {
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
        const wrapper = shallowMount(MstServiceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstService = mstServiceSample;
        mstServiceServiceStub.update.resolves(mstServiceSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstServiceServiceStub.update.calledWith(mstServiceSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstServiceServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstServiceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstService = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstServiceServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstServiceServiceStub.find.resolves(mstServiceSample);
        mstServiceServiceStub.retrieve.resolves([mstServiceSample]);

        // WHEN
        route = {
          params: {
            mstServiceId: '' + mstServiceSample.id,
          },
        };
        const wrapper = shallowMount(MstServiceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstService).toMatchObject(mstServiceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstServiceServiceStub.find.resolves(mstServiceSample);
        const wrapper = shallowMount(MstServiceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
