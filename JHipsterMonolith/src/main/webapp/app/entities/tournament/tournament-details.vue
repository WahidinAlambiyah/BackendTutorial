<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="tournament">
        <h2 class="jh-entity-heading" data-cy="tournamentDetailsHeading">
          <span v-text="t$('jHipsterMonolithApp.tournament.detail.title')"></span> {{ tournament.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jHipsterMonolithApp.tournament.name')"></span>
          </dt>
          <dd>
            <span>{{ tournament.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.tournament.type')"></span>
          </dt>
          <dd>
            <span v-text="t$('jHipsterMonolithApp.TournamentType.' + tournament.type)"></span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.tournament.prizeAmount')"></span>
          </dt>
          <dd>
            <span>{{ tournament.prizeAmount }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.tournament.startDate')"></span>
          </dt>
          <dd>
            <span v-if="tournament.startDate">{{ formatDateLong(tournament.startDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.tournament.endDate')"></span>
          </dt>
          <dd>
            <span v-if="tournament.endDate">{{ formatDateLong(tournament.endDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.tournament.location')"></span>
          </dt>
          <dd>
            <span>{{ tournament.location }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.tournament.maxParticipants')"></span>
          </dt>
          <dd>
            <span>{{ tournament.maxParticipants }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.tournament.status')"></span>
          </dt>
          <dd>
            <span v-text="t$('jHipsterMonolithApp.TournamentStatus.' + tournament.status)"></span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.tournament.event')"></span>
          </dt>
          <dd>
            <div v-if="tournament.event">
              <router-link :to="{ name: 'EventView', params: { eventId: tournament.event.id } }">{{ tournament.event.title }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="tournament.id"
          :to="{ name: 'TournamentEdit', params: { tournamentId: tournament.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./tournament-details.component.ts"></script>
