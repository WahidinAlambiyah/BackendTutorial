/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Testimonial from './testimonial.vue';
import TestimonialService from './testimonial.service';
import AlertService from '@/shared/alert/alert.service';

type TestimonialComponentType = InstanceType<typeof Testimonial>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Testimonial Management Component', () => {
    let testimonialServiceStub: SinonStubbedInstance<TestimonialService>;
    let mountOptions: MountingOptions<TestimonialComponentType>['global'];

    beforeEach(() => {
      testimonialServiceStub = sinon.createStubInstance<TestimonialService>(TestimonialService);
      testimonialServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          testimonialService: () => testimonialServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        testimonialServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Testimonial, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(testimonialServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.testimonials[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: TestimonialComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Testimonial, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        testimonialServiceStub.retrieve.reset();
        testimonialServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        testimonialServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeTestimonial();
        await comp.$nextTick(); // clear components

        // THEN
        expect(testimonialServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(testimonialServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
