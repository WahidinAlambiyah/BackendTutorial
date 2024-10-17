/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import TrxTestimonial from './trx-testimonial.vue';
import TrxTestimonialService from './trx-testimonial.service';
import AlertService from '@/shared/alert/alert.service';

type TrxTestimonialComponentType = InstanceType<typeof TrxTestimonial>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('TrxTestimonial Management Component', () => {
    let trxTestimonialServiceStub: SinonStubbedInstance<TrxTestimonialService>;
    let mountOptions: MountingOptions<TrxTestimonialComponentType>['global'];

    beforeEach(() => {
      trxTestimonialServiceStub = sinon.createStubInstance<TrxTestimonialService>(TrxTestimonialService);
      trxTestimonialServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'jhi-sort-indicator': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          trxTestimonialService: () => trxTestimonialServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxTestimonialServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(TrxTestimonial, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxTestimonialServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.trxTestimonials[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(TrxTestimonial, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxTestimonialServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: TrxTestimonialComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(TrxTestimonial, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        trxTestimonialServiceStub.retrieve.reset();
        trxTestimonialServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        trxTestimonialServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(trxTestimonialServiceStub.retrieve.called).toBeTruthy();
        expect(comp.trxTestimonials[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(trxTestimonialServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        trxTestimonialServiceStub.retrieve.reset();
        trxTestimonialServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(trxTestimonialServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.trxTestimonials[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(trxTestimonialServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        trxTestimonialServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeTrxTestimonial();
        await comp.$nextTick(); // clear components

        // THEN
        expect(trxTestimonialServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(trxTestimonialServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
