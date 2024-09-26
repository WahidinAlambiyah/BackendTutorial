/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TrxTestimonialUpdate from './trx-testimonial-update.vue';
import TrxTestimonialService from './trx-testimonial.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type TrxTestimonialUpdateComponentType = InstanceType<typeof TrxTestimonialUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxTestimonialSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TrxTestimonialUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TrxTestimonial Management Update Component', () => {
    let comp: TrxTestimonialUpdateComponentType;
    let trxTestimonialServiceStub: SinonStubbedInstance<TrxTestimonialService>;

    beforeEach(() => {
      route = {};
      trxTestimonialServiceStub = sinon.createStubInstance<TrxTestimonialService>(TrxTestimonialService);
      trxTestimonialServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          trxTestimonialService: () => trxTestimonialServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(TrxTestimonialUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(TrxTestimonialUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxTestimonial = trxTestimonialSample;
        trxTestimonialServiceStub.update.resolves(trxTestimonialSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxTestimonialServiceStub.update.calledWith(trxTestimonialSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        trxTestimonialServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TrxTestimonialUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxTestimonial = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxTestimonialServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        trxTestimonialServiceStub.find.resolves(trxTestimonialSample);
        trxTestimonialServiceStub.retrieve.resolves([trxTestimonialSample]);

        // WHEN
        route = {
          params: {
            trxTestimonialId: '' + trxTestimonialSample.id,
          },
        };
        const wrapper = shallowMount(TrxTestimonialUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.trxTestimonial).toMatchObject(trxTestimonialSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxTestimonialServiceStub.find.resolves(trxTestimonialSample);
        const wrapper = shallowMount(TrxTestimonialUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
