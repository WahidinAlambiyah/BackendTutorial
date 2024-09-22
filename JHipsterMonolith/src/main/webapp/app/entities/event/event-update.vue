<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="jHipsterMonolithApp.event.home.createOrEditLabel"
          data-cy="EventCreateUpdateHeading"
          v-text="t$('jHipsterMonolithApp.event.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="event.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="event.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.event.title')" for="event-title"></label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="event-title"
              data-cy="title"
              :class="{ valid: !v$.title.$invalid, invalid: v$.title.$invalid }"
              v-model="v$.title.$model"
              required
            />
            <div v-if="v$.title.$anyDirty && v$.title.$invalid">
              <small class="form-text text-danger" v-for="error of v$.title.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.event.description')" for="event-description"></label>
            <textarea
              class="form-control"
              name="description"
              id="event-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.event.date')" for="event-date"></label>
            <div class="d-flex">
              <input
                id="event-date"
                data-cy="date"
                type="datetime-local"
                class="form-control"
                name="date"
                :class="{ valid: !v$.date.$invalid, invalid: v$.date.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.date.$model)"
                @change="updateInstantField('date', $event)"
              />
            </div>
            <div v-if="v$.date.$anyDirty && v$.date.$invalid">
              <small class="form-text text-danger" v-for="error of v$.date.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.event.location')" for="event-location"></label>
            <input
              type="text"
              class="form-control"
              name="location"
              id="event-location"
              data-cy="location"
              :class="{ valid: !v$.location.$invalid, invalid: v$.location.$invalid }"
              v-model="v$.location.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.event.capacity')" for="event-capacity"></label>
            <input
              type="number"
              class="form-control"
              name="capacity"
              id="event-capacity"
              data-cy="capacity"
              :class="{ valid: !v$.capacity.$invalid, invalid: v$.capacity.$invalid }"
              v-model.number="v$.capacity.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.event.price')" for="event-price"></label>
            <input
              type="number"
              class="form-control"
              name="price"
              id="event-price"
              data-cy="price"
              :class="{ valid: !v$.price.$invalid, invalid: v$.price.$invalid }"
              v-model.number="v$.price.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.event.status')" for="event-status"></label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="event-status"
              data-cy="status"
            >
              <option
                v-for="eventStatus in eventStatusValues"
                :key="eventStatus"
                v-bind:value="eventStatus"
                v-bind:label="t$('jHipsterMonolithApp.EventStatus.' + eventStatus)"
              >
                {{ eventStatus }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.event.service')" for="event-service"></label>
            <select class="form-control" id="event-service" data-cy="service" name="service" v-model="event.service">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="event.service && serviceOption.id === event.service.id ? event.service : serviceOption"
                v-for="serviceOption in services"
                :key="serviceOption.id"
              >
                {{ serviceOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.event.testimonial')" for="event-testimonial"></label>
            <select class="form-control" id="event-testimonial" data-cy="testimonial" name="testimonial" v-model="event.testimonial">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="event.testimonial && testimonialOption.id === event.testimonial.id ? event.testimonial : testimonialOption"
                v-for="testimonialOption in testimonials"
                :key="testimonialOption.id"
              >
                {{ testimonialOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./event-update.component.ts"></script>
