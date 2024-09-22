/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Tournament from './tournament.vue';
import TournamentService from './tournament.service';
import AlertService from '@/shared/alert/alert.service';

type TournamentComponentType = InstanceType<typeof Tournament>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Tournament Management Component', () => {
    let tournamentServiceStub: SinonStubbedInstance<TournamentService>;
    let mountOptions: MountingOptions<TournamentComponentType>['global'];

    beforeEach(() => {
      tournamentServiceStub = sinon.createStubInstance<TournamentService>(TournamentService);
      tournamentServiceStub.retrieve.resolves({ headers: {} });

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
          tournamentService: () => tournamentServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        tournamentServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Tournament, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(tournamentServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.tournaments[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: TournamentComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Tournament, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        tournamentServiceStub.retrieve.reset();
        tournamentServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        tournamentServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeTournament();
        await comp.$nextTick(); // clear components

        // THEN
        expect(tournamentServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(tournamentServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
