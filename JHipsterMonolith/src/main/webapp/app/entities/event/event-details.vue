<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="event">
        <h2 class="jh-entity-heading" data-cy="eventDetailsHeading">
          <span v-text="t$('jHipsterMonolithApp.event.detail.title')"></span> {{ event.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jHipsterMonolithApp.event.title')"></span>
          </dt>
          <dd>
            <span>{{ event.title }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.event.description')"></span>
          </dt>
          <dd>
            <span>{{ event.description }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.event.date')"></span>
          </dt>
          <dd>
            <span v-if="event.date">{{ formatDateLong(event.date) }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.event.location')"></span>
          </dt>
          <dd>
            <span>{{ event.location }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.event.capacity')"></span>
          </dt>
          <dd>
            <span>{{ event.capacity }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.event.price')"></span>
          </dt>
          <dd>
            <span>{{ event.price }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.event.status')"></span>
          </dt>
          <dd>
            <span v-text="t$('jHipsterMonolithApp.EventStatus.' + event.status)"></span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.event.service')"></span>
          </dt>
          <dd>
            <div v-if="event.service">
              <router-link :to="{ name: 'ServiceView', params: { serviceId: event.service.id } }">{{ event.service.id }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.event.testimonial')"></span>
          </dt>
          <dd>
            <div v-if="event.testimonial">
              <router-link :to="{ name: 'TestimonialView', params: { testimonialId: event.testimonial.id } }">{{
                event.testimonial.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="event.id" :to="{ name: 'EventEdit', params: { eventId: event.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./event-details.component.ts"></script>
